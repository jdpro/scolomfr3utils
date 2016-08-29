package fr.apiscol.metadata.scolomfr3utils.skos;

import com.hp.hpl.jena.rdf.model.Model;

public interface ISkosApi {

	Model getSkosModel();

	void setSkosModel(Model skosModel);

	/**
	 * 
	 * @param URI
	 *            URI of subject resource
	 * @return prefLabel or "NO_RESULT"
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

	/**
	 * 
	 * @param URI
	 *            URI of resource
	 * @return true if this resource is the subject of an assertion in skos
	 *         vocabulary
	 */
	boolean resourceExists(String Uri);

	/**
	 * 
	 * @param URI
	 *            URI of vocabulary
	 * @return true if this vocabulary is referenced in skos
	 */
	boolean vocabularyExists(String Uri);

}
