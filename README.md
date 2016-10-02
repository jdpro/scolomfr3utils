# Scolomfr3utils
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

```java
Scolomfr3Utils scolomfrUtils = new Scolomfr3Utils();
scolomfrUtils.setScolomfrVersion("3.0");
scolomfrUtils.checkXsd().checkLabels().checkClassifications();
if (!scolomfrUtils.isValid()) {
			List<String> messages = scolomfrUtils
					.getMessages(MessageStatus.FAILURE);
     ...
```

#### Non-java projects

Download scolomfr3utils stand-alone jar and execute it from command line.

```shell
java -jar target/scolomfr3utils-0.0.1-jar-with-dependencies.jar -f /path/to/my/scolomfr/file.xml -v 3.0 --check-all
```

```shell
XSD validation success
Loading skos file from /scolomfr/v30/scolomfr.skos
-------------------------------------------------------
Validation result : FAILURE
-------------------------------------------------------
FAILURE MESSAGES
-------------------------------------------------------
Resource label "logiciel organiseur d’idées" line 394 does not match any label of uri "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-020-num-057"
Vocabulary "Cadre européen commun de référence pour les langues" line 501 does not match vocabulary of taxons used in classification : it should be URI "http://data.education.fr/voc/scolomfr/scolomfr-voc-042" or label "ScoLomFr Vocabulaire42".
-------------------------------------------------------
WARNING MESSAGES
-------------------------------------------------------
Entity line 191 : ENCODING parameter value ("8BIT") is not supported by this vCard version.
Entity line 191 : CHARSET parameter is not supported in this vCard version.
```


### Requirements
