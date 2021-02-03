package it.cnr.istc.stlab.ontology.versionmanager;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntologyVersionManager {

	private static Logger logger = LoggerFactory.getLogger(OntologyVersionManager.class);

	private static final String FOLDER = "f";
	private static final String VERSION_NUMBER = "v";
	private static final String ONTOLOGY_NAME = "o";

	public static void main(String[] args) throws IOException {
		logger.info("Ontology Version Manager - 0.0.1");

		Options options = new Options();

		options.addOption(Option.builder(FOLDER).argName("filepath").hasArg().required(true)
				.desc("The folder filepath where the ontology network is stored.").longOpt("folder").build());

		options.addOption(Option.builder(VERSION_NUMBER).argName("Major|Minor|Patch").hasArg().required(false)
				.desc("The numer of the version ID to progress. [Default: Patch]").longOpt("version").build());

		options.addOption(Option.builder(ONTOLOGY_NAME).argName("filename").hasArg().required(false)
				.desc("The name of the ontology files [Default: ontology.owl]").longOpt("ontology-filename").build());

		CommandLine commandLine = null;

		CommandLineParser cmdLineParser = new DefaultParser();
		try {
			commandLine = cmdLineParser.parse(options, args);

			if (!commandLine.hasOption(FOLDER)) {
				printOptions(options);
			}

			String folder = commandLine.getOptionValue(FOLDER);

			ProgressVersion.Version v = ProgressVersion.Version.Patch;

			if (commandLine.hasOption(VERSION_NUMBER)) {
				v = ProgressVersion.Version.valueOf(commandLine.getOptionValue(VERSION_NUMBER));
			}

			String ontologyName = "ontology.owl";
			if (commandLine.hasOption(ONTOLOGY_NAME)) {
				ontologyName = commandLine.getOptionValue(ONTOLOGY_NAME);
			}

			ProgressVersion pv = new ProgressVersion(folder, ontologyName, v);
			pv.run();

		} catch (ParseException e) {
			printOptions(options);
		}

	}

	private static void printOptions(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar ontology.versionmanager-0.0.1.jar [ARGS]", options);
	}
}
