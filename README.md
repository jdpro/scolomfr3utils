# scolomfr3utils
 Set of utilities to improve the quality of scoLOMFR3 learning resource metadata

## Purpose

 Only xsd validation is currently available to control scolomfr3 metadata. However, these metadata refer to vocabularies published in the semantic web formats (RDF, SKOS).
 
 The aim of scolomfr3utils project, for lack of other available technology, is to improve the coherence of scolomfr3 metadata both at the syntactic (XSD) and semantic level.
 
## Benefits


### Quality

A tool to ensure the quality of data needs to offer itself quality guarantees...
* Scolomfr3utils source code quality is managed by Sonarqube
* Scolomfr3utils is covered by unit and integration tests at a minimum level of 80%

### Portability
Scolomfr3utils is likely to be used in both Java and non Java projects.

#### Java projects

Add scolomfr3utils jar to your project dependencies [from Maven Central](http://search.maven.org/#search|ga|1|g%3A%22fr.apiscol.metadata%22) and use the Java programming API.

```
Scolomfr3Utils scolomfrUtils = new Scolomfr3Utils();
scolomfrUtils.setScolomfrVersion("3.0");
scolomfrUtils.checkXsd().checkLabels().checkClassifications();
if (!scolomfrUtils.isValid()) {
			List<String> messages = scolomfrUtils
					.getMessages(MessageStatus.FAILURE);
     ...
```

