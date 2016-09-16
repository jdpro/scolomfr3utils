package fr.apiscol.metadata.scolomfr3utils.skos;

import com.hp.hpl.jena.rdf.model.Model;

public interface ISkosApi {

	Model getSkosModel();

	void setSkosModel(Model skosModel);

	/**
	 * 
	 * @param Uri
	 *            URI of subject resource
	 * @return prefLabel or "NO_RESULT"
	 */
	String getPrefLabelForResource(String Uri);

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
	 * @param Uri URI of resource
	 * @return true if this resource is the subject of an assertion in skos
	 *         vocabulary
	 */
	boolean resourceExists(String Uri);

	/**
	 * 
	 * @param Uri URI of vocabulary
	 * @return true if this vocabulary is referenced in skos
	 */
	boolean vocabularyExists(String Uri);

	/**
	 * 
	 * @param resourceUri URI of the resource
	 * @param vocabUri URI of vocabulary
	 * @return true if the resource belongs to the vocabulary
	 */
	boolean resourceIsMemberOfVocabulary(String resourceUri, String vocabUri);

	/**
	 * 
	 * @param resourceUri URI of the resource
	 * @param resourceLabel label to investigate
	 * @return true if the label is the preflabel or one of the altlabels of
	 *         this uri
	 */
	boolean resourceHasLabel(String resourceUri, String resourceLabel);

}
