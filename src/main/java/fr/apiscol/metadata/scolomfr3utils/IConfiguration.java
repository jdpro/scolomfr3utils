package fr.apiscol.metadata.scolomfr3utils;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 
 * Wrapper for xml configuration file
 *
 */
public interface IConfiguration {

	/**
	 * 
	 * @return a map of command line options with corresponding method name of
	 *         {@link Scolomfr3Utils} and help message
	 */
	Map<String, Pair<String, String>> commandLineOptions();

	/**
	 * 
	 * @param scolomfrVersion
	 *            Major.minor version of scolomfr schema to be used
	 * @return Skos file path (relative to classpath)
	 */
	String getSkosFilePath(String scolomfrVersion);

	/**
	 * 
	 * @param scolomfrVersion
	 *            Major.minor version of scolomfr schema to be used
	 * @return Xsd file path (relative to classpath)
	 */
	String getXsdFilePath(String scolomfrVersion);

	/**
	 * 
	 * @param scolomfrVersion
	 *            Major.minor version of scolomfr schema to be used
	 * @return True if version is supported
	 */
	boolean versionIsSupported(String scolomfrVersion);

}
