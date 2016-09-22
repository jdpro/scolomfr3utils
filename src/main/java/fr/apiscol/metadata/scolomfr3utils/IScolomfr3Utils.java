package fr.apiscol.metadata.scolomfr3utils;

import java.io.File;
import java.util.List;

import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;

/**
 * 
 * Main interface of the library
 *
 */
public interface IScolomfr3Utils {

	/**
	 * Manually reset the status and the list of messages Automatic reset is
	 * performed each time you change scolomfr file input or scolomfr schema
	 * version.
	 */
	void reset();

	/**
	 * Returns a wrapper for jena Model of Skos vocabulary
	 * 
	 * @return an API to skos vocabulary
	 */
	ISkosApi getSkosApi();

	/**
	 * Get all messages by status
	 * 
	 * @param status
	 *            Status of message to return
	 * @return a list of messages generated by processes
	 */
	List<String> getMessages(MessageStatus status);

	/**
	 * Provide a scolomfr XML file for features that requires a document
	 * 
	 * @param scolomfrFile
	 *            The file to be handled
	 */
	public void setScolomfrFile(File scolomfrFile);

	/**
	 * Indicates if ScoLomfr file is valid after validation process has occurred
	 * 
	 * @return the result of validation
	 */
	public Boolean isValid();

	/**
	 * Specify the schema version to be used
	 * 
	 * @param version
	 *            Major.minor version
	 */
	public void setScolomfrVersion(String version);

	/**
	 * Apply xsd validation
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	public IScolomfr3Utils checkXsd();

	/**
	 * Apply all kinds of validation
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	public IScolomfr3Utils checkAll();

	/**
	 * Check classification field
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	IScolomfr3Utils checkClassifications();

	/**
	 * Check taxon paths - consecutive elements must be connected by broader
	 * relations in skos
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	IScolomfr3Utils checkTaxonPaths();

	/**
	 * Check classification purposes -
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	IScolomfr3Utils checkClassificationPurposes();

	/**
	 * Check that all taxon belong to taxonpath source vocabulary
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	IScolomfr3Utils checkTaxonPathVocabs();

	/**
	 * Check that all labels match provided URI in skos vocabulary
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	IScolomfr3Utils checkLabels();

	/**
	 * check that vcards are RFC 2426 compliant
	 * 
	 * @return {@link IScolomfr3Utils} for concatenation
	 */
	IScolomfr3Utils checkVcards();
}
