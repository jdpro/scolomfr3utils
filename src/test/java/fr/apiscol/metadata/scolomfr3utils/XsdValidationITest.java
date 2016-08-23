package fr.apiscol.metadata.scolomfr3utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.command.check.XsdValidationCommandTest;

/**
 * Test xsd validation with different kinds of files
 */
public class XsdValidationITest {
	private static final String MISSING_SCOLOMFR_FILE_FAILURE_MESSAGE = "Please provide a scolomfr file before calling scolomfrutils methods.";
	private Scolomfr3Utils scolomfrutils;

	@Before
	public void setup() {
		scolomfrutils = new Scolomfr3Utils();
		scolomfrutils.setScolomfrVersion("3.0");
	}

	@Test
	public void testXsdValidationSuccess() {
		File scolomfrFile = new File("src/test/data/3.0/any/valid/exemple.xml");
		scolomfrutils.setScolomfrFile(scolomfrFile);
		scolomfrutils.checkXsd();
		assertTrue("There should be no validation messages", scolomfrutils.getMessages().isEmpty());
		assertTrue("Result of validation with the official exemple file should be valid", scolomfrutils.isValid());
	}

	@Test
	public void testXsdValidationFailure() {
		File scolomfrFile = new File("src/test/data/3.0/1/invalid/double-general.xml");
		scolomfrutils.setScolomfrFile(scolomfrFile);
		scolomfrutils.checkXsd();
		assertTrue("The validation messages should contain : " + XsdValidationCommandTest.DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE,
				scolomfrutils.getMessages().contains(XsdValidationCommandTest.DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE));
		assertFalse("Result of validation with duplicate general element should not be valid", scolomfrutils.isValid());
	}

	@Test
	public void testXsdWithoutFile() {
		scolomfrutils.checkXsd();
		assertTrue("The validation messages should contain : " + MISSING_SCOLOMFR_FILE_FAILURE_MESSAGE,
				scolomfrutils.getMessages().contains(MISSING_SCOLOMFR_FILE_FAILURE_MESSAGE));
		assertFalse("Result of validation is valid", scolomfrutils.isValid());
	}
}
