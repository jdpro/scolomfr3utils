package fr.apiscol.metadata.scolomfr3utils.skos;

import org.apache.jena.rdf.model.Model;

public interface ISkosApi {

	Model getSkosModel();

	void setSkosModel(Model skosModel);

	String getPrefLabelForResource(String URI);

	boolean isBroaderThan(String subjectURI, String objectURI);

}
