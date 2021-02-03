# Ontology Version Manager (OVM)

OVM is a tool that allows you to update version IRI of an ontology network.
The tool assumes that:
- all the ontologies composing the network (also called modules) are contained within the same folder (or in its subfolders);
- all the modules have the same filename;
- all the modules declare a version IRI;
- the version IRI comply with the pattern  ``[Ontology IRI]/[Major].[Minor].[Patch]`` where Major, Minor and Patch are integers defined according to [semantic versioning rules](https://semver.org/).

An example of such ontology network can be found in [example](example) folder.

OVM is able to progress Major, Minor or Path integer of one unit.

OVM is distributed as JAR files that can be downloaded from the [Releases page.](https://github.com/extreme-design/ontology.versionmanager/releases)
The jar can be used as follows

```
usage: java -jar ontology.versionmanager-0.0.1.jar [ARGS]
 -f,--folder <filepath>              The folder filepath where the
                                     ontology network is stored.
 -o,--ontology-filename <filename>   The name of the ontology files
                                     [Default: ontology.owl]
 -v,--version <Major|Minor|Patch>    The numer of the version ID to
                                     progress. [Default: Patch]
```

## License

This tool is distributed under [Apache 2.0 License](LICENSE).

