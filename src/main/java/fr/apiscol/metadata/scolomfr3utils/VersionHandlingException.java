package fr.apiscol.metadata.scolomfr3utils;

/**
 * Thrown if the initialization problem is related to requested version : either
 * it does not exists or is not supported by the current version of
 * scolomfrutils, either there's an internal configuration problem for this
 * version.
 */
public class VersionHandlingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param string
	 *            Version problem explanation
	 */
	public VersionHandlingException(String string) {
		super(string);
	}

}
