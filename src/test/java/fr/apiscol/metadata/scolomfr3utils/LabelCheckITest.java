package fr.apiscol.metadata.scolomfr3utils;

import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;

/**
 * Test xsd validation with different kinds of files
 */
public class LabelCheckITest {
	private Scolomfr3Utils scolomfrutils;

	@Before
	public void setup() {
		scolomfrutils = new Scolomfr3Utils();
		scolomfrutils.setScolomfrVersion("3.0");
	}

	@Test
	public void testTaxonPathCheckFailure() {
		File scolomfrFile = new File("src/test/data/3.0/labels/invalid/exemple.xml");
		scolomfrutils.setScolomfrFile(scolomfrFile);
		scolomfrutils.checkAll();
		assertFalse("There should be failure messages", scolomfrutils.getMessages(MessageStatus.FAILURE).isEmpty());
		assertFalse("Result of validation with the official exemple file should not be valid", scolomfrutils.isValid());
	}

}
