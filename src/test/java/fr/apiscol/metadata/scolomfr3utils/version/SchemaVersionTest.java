package fr.apiscol.metadata.scolomfr3utils.version;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;

/**
 * Test schema version type behavior
 */
public class SchemaVersionTest {

	@Test
	public void testSchemaVersionStringification() throws Exception {
		int major = 3;
		int minor = 1;
		String expectedString = major + "." + minor;
		SchemaVersion schemaVersion = new SchemaVersion(major, minor);
		assertTrue("Schema version as string should be " + expectedString + "and not " + schemaVersion.toString(),
				StringUtils.equals(expectedString, schemaVersion.toString()));

	}

	@Test
	public void testSchemaVersionEquality() throws Exception {
		int major = 3;
		int minor = 1;
		SchemaVersion schemaVersion1 = new SchemaVersion(major, minor);
		SchemaVersion schemaVersion2 = new SchemaVersion();
		schemaVersion2.setMajor(major);
		schemaVersion2.setMinor(minor);
		assertTrue("Schema version 1 and 2 should be equals ", schemaVersion1.equals(schemaVersion2));

	}

	@Test
	public void testSchemaVersionComparaisonOnMinor() throws Exception {
		int major1 = 3;
		int minor1 = 0;
		int major2 = 3;
		int minor2 = 1;
		SchemaVersion schemaVersion1 = new SchemaVersion(major1, minor1);
		SchemaVersion schemaVersion2 = new SchemaVersion(major2, minor2);
		assertTrue("Schema version 1 sould be inferior to schema version 2",
				schemaVersion1.compareTo(schemaVersion2) < 0);

	}

	@Test
	public void testSchemaVersionFromString() throws Exception {
		int major = 3;
		int minor = 1;
		SchemaVersion schemaVersion1 = new SchemaVersion(major, minor);
		SchemaVersion schemaVersion2 = SchemaVersion.fromString("SCOLOMFRv" + major + "." + minor);
		assertTrue("Schema version 1 and 2 should be equals, major should be " + major + " and minor " + minor
				+ " schema version 2 has major " + schemaVersion2.getMajor() + " and minor "
				+ schemaVersion2.getMinor(), schemaVersion1.equals(schemaVersion2));

	}

}
