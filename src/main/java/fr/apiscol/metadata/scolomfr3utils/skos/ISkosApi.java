package fr.apiscol.metadata.scolomfr3utils.skos;

import java.util.List;

import org.w3c.dom.Node;

import com.hp.hpl.jena.rdf.model.Model;

public interface ISkosApi {

	Model getSkosModel();

	void setSkosModel(Model skosModel);

	/**
	 * 
	 * @param uri
	 *            URI of subject resource
	 * @return prefLabel or "NO_RESULT"
	 */
	String getPrefLabelForResource(String uri);

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
	 * @param uri
	 *            URI of resource
	 * @return true if this resource is the subject of an assertion in skos
	 *         vocabulary
	 */
	boolean resourceExists(String uri);

	/**
	 * 
	 * @param uri
	 *            URI of vocabulary
	 * @return true if this vocabulary is referenced in skos
	 */
	boolean vocabularyExists(String uri);

	/**
	 * 
	 * @param resourceUri
	 *            URI of the resource
	 * @param vocabUri
	 *            URI of vocabulary
	 * @return true if the resource belongs to the vocabulary
	 */
	boolean resourceIsMemberOfVocabulary(String resourceUri, String vocabUri);

	/**
	 * 
	 * @param resourceUri
	 *            URI of the resource
	 * @param resourceLabel
	 *            label to investigate
	 * @return true if the label is the preflabel or one of the altlabels of
	 *         this uri
	 */
	boolean resourceHasLabel(String resourceUri, String resourceLabel);

	/**
	 * 
	 * @param vocabLabel
	 *            a prefLabl or altLabel
	 * @return null if no vocabulary has this label
	 */
	String getVocabUriByLabel(String vocabLabel);

	/**
	 * 
	 * @param taxonNodes
	 *            list of taxon nodes
	 * @return URI of common vocabulary, else null
	 */
	String detectCommonVocabularyForTaxonNodes(List<Node> taxonNodes);

	/**
	 * 
	 * @param resourceUri
	 * @return URI of vocabulary this resource belongs to
	 */
	String getResourceVocabulary(String resourceUri);

}
