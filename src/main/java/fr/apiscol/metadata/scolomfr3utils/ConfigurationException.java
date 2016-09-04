package fr.apiscol.metadata.scolomfr3utils;

/**
 * Thrown when scolomfr3utils is unable to read is own configuration. Config file
 * is embedded in jar, if tests have been launched before compilation, this
 * exception may never occur.
 */
public class ConfigurationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param e
	 *            The source exception
	 */
	public ConfigurationException(Exception e) {
		super(e);
	}

}
