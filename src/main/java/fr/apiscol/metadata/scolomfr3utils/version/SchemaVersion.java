package fr.apiscol.metadata.scolomfr3utils.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Versions of scoLOMfr schema
 */
public class SchemaVersion implements Comparable<SchemaVersion> {
	private static final Pattern versionPattern = Pattern.compile(".*([0-9]+)\\.([0-9]+)");
	static final String UNDEFINED = "undefined version";
	private int minor = -1;
	private int major = -1;

	/**
	 * Build a schema version by providing major and minor as integers
	 * 
	 * @param major
	 *            major version
	 * @param minor
	 *            minor version
	 */
	public SchemaVersion(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	/**
	 * Build a void schema version
	 * 
	 */
	public SchemaVersion() {
		// void constructor
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	@Override
	public String toString() {
		if (minor == -1 || major == -1) {
			return UNDEFINED;
		}
		return String.format("%d.%d", major, minor);

	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(major).append(minor).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SchemaVersion) {
			return getMajor() == ((SchemaVersion) other).getMajor() && getMinor() == ((SchemaVersion) other).getMinor();
		}
		return false;
	}

	@Override
	public int compareTo(SchemaVersion other) {
		if (other == null || other.getMajor() < getMajor()) {
			return 1;
		}
		if (other.getMajor() > getMajor()) {
			return -1;
		}

		if (other.getMinor() > getMinor()) {
			return -1;
		}
		if (other.getMinor() < getMinor()) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * Guess version from any string containing major.minor schema
	 * 
	 * @param versionStr
	 *            version as string, e.g. "SCOLOMFRv2.0"
	 * @return null if provided string does not match pattern major.minor
	 */
	public static SchemaVersion fromString(String versionStr) {
		SchemaVersion version = null;
		Matcher m = versionPattern.matcher(versionStr);
		if (m.matches()) {
			int major = Integer.parseInt(m.group(1));
			int minor = Integer.parseInt(m.group(2));
			version = new SchemaVersion(major, minor);
		}
		return version;
	}
}
