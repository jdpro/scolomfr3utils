package fr.apiscol.metadata.scolomfr3utils.skos;

import com.hp.hpl.jena.rdf.model.Model;

public interface ISkosApi {

	Model getSkosModel();

	void setSkosModel(Model skosModel);

	/**
	 * 
	 * @param URI
	 * @return
	 */
	String getPrefLabelForResource(String URI);

	/**
	 * Check if a broader relation exists from subject to object
	 * 
	 * @param subjectURI
	 *            URI of subject resource
	 * @param objectURI
	 *            URI of object resource
	 * @return true if a broader relation exists from subject to object
	 */
	boolean isBroaderThan(String subjectURI, String objectURI);

}
