package fr.apiscol.metadata.scolomfr3utils;

import java.io.File;
import java.util.List;

import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;

/**
 * 
 * Main interface of the library
 *
 */
public interface IScolomfr3Utils {

	/**
	 * Provide a scolomfr XML file for features that requires a document
	 * 
	 * @param scolomfrFile
	 *            The file to be handled
	 */
	public void setScolomfrFile(File scolomfrFile);

	/**
	 * Get all messages generated by processes
	 * 
	 * @return a list of messages generated by processes
	 */
	public List<String> getMessages();

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
	 * @return
	 */
	IScolomfr3Utils checkTaxonPaths();

	/**
	 * Returns a wrapper for jena Model of Skos vocabulary
	 * 
	 * @return an API to skos vocabulary
	 */
	ISkosApi getSkosApi();
}
