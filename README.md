# Ontology Version Manager (OVM)

OVM is a tool that allows you to update version IRI of an ontology network.
The tool assumes that:
- all the ontologies composing the network (also called modules) are contained within the same folder (or in its subfolders);
- all the modules have the same filename;
- all the module declare a version IRI;
- the version IRI comply with the pattern  ``[Ontology IRI]/[Major].[Minor].[Patch]`` where Major, Minor and Patch are integers defined according to [semantic versioning rules](https://semver.org/).

An example of such ontology network can be found in [example](example) folder.



