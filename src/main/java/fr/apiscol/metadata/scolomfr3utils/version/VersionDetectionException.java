package fr.apiscol.metadata.scolomfr3utils.version;

/**
 * Thrown if system is anable to detect scolmfr version from profided document
 */
public class VersionDetectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VersionDetectionException(Throwable cause) {
		super(cause);
	}

	public VersionDetectionException(String message) {
		super(message);
	}

}
