package it.cnr.istc.stlab.ontology.versionmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.OWL2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressVersion {

	private String folderPath;
	private String ontologyfilename;
	private Version versionToProgress;

	public enum Version {
		Major, Minor, Patch
	}

	public final static String DEFAULT_ONTOLOGY_FILENAME = "ontology.owl";
	private final static Logger logger = LoggerFactory.getLogger(ProgressVersion.class);

	public ProgressVersion(String folderPath) {
		this(folderPath, DEFAULT_ONTOLOGY_FILENAME, Version.Minor);
	}

	public ProgressVersion(String folderPath, String ontologyfilename, Version v) {
		this.ontologyfilename = ontologyfilename;
		this.folderPath = folderPath;
		this.versionToProgress = v;
	}

	public void run() throws IOException {
		Set<String> ontologies = new HashSet<>();
		Files.walk(Paths.get(this.folderPath)).filter(Files::isRegularFile)
				.filter(p -> FilenameUtils.getName(p.toFile().getName()).equals(ontologyfilename)).forEach(p -> {
					try {
						ontologies.add(getVersionIRI(p.toFile().toString()));
					} catch (Exception e) {
						logger.error("Error with ontology {} - {}", p.toFile().toString(), e.getMessage());
					}
				});

		Files.walk(Paths.get(this.folderPath)).filter(Files::isRegularFile)
				.filter(p -> FilenameUtils.getName(p.toFile().getName()).equals(ontologyfilename)).forEach(p -> {
					try {
						progressVersions(p.toFile().getAbsolutePath(), ontologies);
					} catch (Exception e) {
						logger.error("Error with ontology {} - {}", p.toFile().toString(), e.getMessage());
						e.printStackTrace();
					}
				});
	}

	private String progressVersionIRI(String iri) {
		String[] arr = FilenameUtils.getName(iri).split("\\.");
		String base = FilenameUtils.getPath(iri);

		int major = Integer.parseInt(arr[0]);
		int minor = Integer.parseInt(arr[1]);
		int patch = Integer.parseInt(arr[2]);
		switch (versionToProgress) {
		case Major:
			major++;
			break;
		case Minor:
			minor++;
			break;
		default:
		case Patch:
			patch++;
			break;

		}
		return base + major + "." + minor + "." + patch;
	}

	private String getVersionIRI(String filepath) {
		OntModel om = ModelFactory.createOntologyModel();
		RDFDataMgr.read(om, filepath);
		ExtendedIterator<Ontology> ei = om.listOntologies();
		if (ei.hasNext()) {
			Ontology o = om.listOntologies().next();
			StmtIterator si = o.listProperties(OWL2.versionIRI);
			if (si.hasNext()) {
				return si.next().getObject().asResource().getURI();
			}
		}
		return null;
	}

	private void progressVersions(String ontologyFilepath, Set<String> ontologieToProgress)
			throws FileNotFoundException {
		OntModel om = ModelFactory.createOntologyModel();
		RDFDataMgr.read(om, ontologyFilepath);
		ExtendedIterator<Ontology> ei = om.listOntologies();
		if (ei.hasNext()) {
			Ontology o = om.listOntologies().next();
			StmtIterator si = o.listProperties(OWL2.versionIRI);
			String currentVersionIRI = null;
			if (si.hasNext()) {
				currentVersionIRI = si.next().getObject().asResource().getURI();
			}

			String currentPriorVersion = null;
			StmtIterator sPriorVersion = o.listProperties(OWL2.priorVersion);
			if (sPriorVersion.hasNext()) {
				currentPriorVersion = sPriorVersion.next().getObject().asResource().getURI();
			}

			if (currentVersionIRI != null) {
				String newVersionIRI = progressVersionIRI(currentVersionIRI);
				logger.trace("Current {} - Progress {}", currentVersionIRI, newVersionIRI);
				if (currentPriorVersion != null) {
					// remove currentprior version
					om.remove(o, OWL2.priorVersion, om.getResource(currentPriorVersion));
				}

				om.remove(o, OWL2.versionIRI, om.getResource(currentVersionIRI));

				om.add(o, OWL2.versionIRI, om.createResource(newVersionIRI));
				om.add(o, OWL2.priorVersion, om.createResource(currentVersionIRI));

				StmtIterator imports = o.listProperties(OWL2.imports);
				Model toAdd = ModelFactory.createDefaultModel();
				Model toRemove = ModelFactory.createDefaultModel();
				while (imports.hasNext()) {
					Statement s = imports.next();

					if (ontologieToProgress.contains(s.getObject().asResource().getURI())) {
						String newImportURI = progressVersionIRI(s.getObject().asResource().getURI());
						toAdd.add(o, OWL2.imports, om.createResource(newImportURI));
						toRemove.add(o, OWL2.imports, s.getObject().asResource());
					}
				}
				om.remove(toRemove);
				om.add(toAdd);

			}
		}

		om.write(new FileOutputStream(new File(ontologyFilepath)));
	}

}
