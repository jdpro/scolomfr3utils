# Scolomfr3utils
 Set of utilities to improve the quality of scoLOMFR3 learning resource metadata.

## Purpose

 Only xsd validation is currently available to control scoLOMFR3 metadata. However, these metadata refer to vocabularies published in the semantic web formats (RDF, SKOS).
 
 The aim of scolomfr3utils project, for lack of other available technology, is to monitor the coherence of scolomfr3 metadata *both at the syntactic (XSD) and semantic level*.
 
## Benefits

## Quality

A tool to ensure the quality of data needs to offer itself quality guarantees...
* Scolomfr3utils source code quality is *managed by Sonarqube*.
* Scolomfr3utils is covered by unit and integration tests *at an average level of 80%*.

## Portability
Scolomfr3utils is likely to be used in both Java and non-Java projects.

### Windows exécutable version

Download the C++/Qt5 executable wrapper for Windows :
 * [With installer](https://github.com/jdpro/scolomfr3utils/blob/master/etc/scolomfr3utils.exe?raw=true)
 * [Without installer](https://github.com/jdpro/scolomfr3utils/blob/master/etc/scolomfr3utils.zip)

### Java projects

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

### Non-java projects

Download [scolomfr3utils **stand-alone jar**](https://github.com/jdpro/scolomfr3utils/blob/master/etc/stand-alone-jar.zip?raw=true) and execute it from command line.

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
For maven users, the stand-alone jar may be generated in the following way :
```
mvn clean compile assembly:single
```
## Requirements

Scolomfr3utils embeds 2.x versions of Apache Jena which requires **Java 7**. Jena has not been upgraded to 3.X versions to avoid losing Java 7 compatibility.

## Command line API

May be displayed by the standalone jar (-h option)

```shell
$ java -jar target/scolomfr3utils-0.0.1-jar-with-dependencies.jar -h
Option                           Description                                   
------                           -----------                                   
--check-all                      Apply all available validations.              
                                 Equals : --check-xsd --check-classifications --check-vcards --check-labels                                
--check-classification-purposes  Check that taxonpaths are under the right purpose if specified.                                
--check-classifications          Equals : --check-taxonpaths --check-taxonpath- vocabs  --check-classification-purposes           
--check-labels                   Check that labels are either preflabels or altLabels of provided URIs.                                     
--check-taxonpath-vocabs         Check that taxon belong to their vocabulary if specified.                                  
--check-taxonpaths               Check that consecutive taxon are in hierarchical relation                       
--check-vcards                   Check that inline vcards are RFC compliant according to specified vcard version.                  
--check-xsd                      Apply xsd validation                          
-f                               Scolomfr xml file path                        
-h                               Display help                                  
-v                               Scolomfr schema version                       
```
## Future improvements

In the future, a major improvement would be to control the consistency of indexing by discipline, by educational level and by program ("domaine d'enseignement"). For example, a warning should be issued if a resource intended to the 'CM1' class is indexed to the program of the 'Terminale' class or if it is attached to a field that is not taught in CM1.

## Copyright

Copyright (C) 2016 Joachim Dornbusch.

Copyright (C) 2016 Itop Education

Distributed under the GNU/GPL v3 license.
