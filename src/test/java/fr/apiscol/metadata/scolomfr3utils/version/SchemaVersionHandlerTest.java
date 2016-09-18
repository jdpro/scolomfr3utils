package fr.apiscol.metadata.scolomfr3utils.version;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

/**
 * Test schema version type behavior
 */
public class SchemaVersionHandlerTest {

	@Test
	public void testSchemaVersionWithOnlyOneMetadataSchematag() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/version/valid/only-one-metadataschema.xml");
		SchemaVersion version = SchemaVersionHandler.getInstance()
				.getMostRecentVersionFromDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		assertTrue("Schema major version should be 3", version.getMajor() == 3);
		assertTrue("Schema minor version should be 0", version.getMinor() == 0);
	}

	@Test
	public void testSchemaVersionWithTwoMetadataSchematags() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/version/valid/two-metadataschemas.xml");
		SchemaVersion version = SchemaVersionHandler.getInstance()
				.getMostRecentVersionFromDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		assertTrue("Schema major version should be 3", version.getMajor() == 3);
		assertTrue("Schema minor version should be 1", version.getMinor() == 1);
	}

	@Test(expected = VersionDetectionException.class)
	public void testSchemaVersionWithoutMetadataSchematags() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/version/valid/no-metadataschema.xml");
		SchemaVersionHandler.getInstance()
				.getMostRecentVersionFromDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
	}

}
