package fr.apiscol.metadata.scolomfr3utils.check;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.Scolomfr3Utils;

/**
 * Test xsd validation with a valid file
 */
public class XsdValidationSuccessTest {
	private Scolomfr3Utils scolomfrutils;

	@Test
	public void testXsdValidationSuccess() {
		scolomfrutils = new Scolomfr3Utils();
		scolomfrutils.setScolomfrVersion("3.0");
		File scolomfrFile = new File("src/test/data/3.0/valid/exemple.xml");
		assertTrue("The file should be in the data directory", scolomfrFile.exists());
		scolomfrutils.setScolomfrFile(scolomfrFile);
		scolomfrutils.checkXsd();
		assertTrue("There should be no validation messages", scolomfrutils.getMessages().isEmpty());
		assertTrue("Result of validation with the official exemple file should be valid", scolomfrutils.isValid());
	}
}
