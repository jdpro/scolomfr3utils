package fr.apiscol.metadata.scolomfr3utils;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;

/**
 * Test xsd validation with different kinds of files
 */
public class ClassificationPurposeCheckITest {
	private Scolomfr3Utils scolomfrutils;

	@Before
	public void setup() {
		scolomfrutils = new Scolomfr3Utils();
		scolomfrutils.setScolomfrVersion("3.0");
	}

	@Test
	public void testClassificationCheckCheckFailure() {
		File scolomfrFile = new File("src/test/data/3.0/9/invalid/classification-with-wrong-purpose.xml");
		scolomfrutils.setScolomfrFile(scolomfrFile);
		scolomfrutils.checkAll();
		assertFalse("There should be failure messages", scolomfrutils.getMessages(MessageStatus.FAILURE).isEmpty());
		assertFalse("Result of validation with the official exemple file should not be valid", scolomfrutils.isValid());
	}

	@Test
	public void testClassificationCheckSuccess() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/classification-with-right-purpose.xml");
		scolomfrutils.setScolomfrFile(scolomfrFile);
		scolomfrutils.checkAll();
		assertTrue("There should not be failure messages", scolomfrutils.getMessages(MessageStatus.FAILURE).isEmpty());
		assertTrue("Result of validation with the official exemple file should be valid", scolomfrutils.isValid());
	}

}
