package fr.apiscol.metadata.scolomfr3utils.check;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.Scolomfr3Utils;

/**
 * Unit test for simple App.
 */
public class XsdCheckWithoutFileTest {
	private Scolomfr3Utils scolomfrutils;

	@Test
	public void testXsdWithoutFile() {

		scolomfrutils = new Scolomfr3Utils();
		scolomfrutils.setScolomfrVersion("3.0");
		scolomfrutils.checkXsd();

		Iterator<String> it = scolomfrutils.getMessages().iterator();
		assertTrue(scolomfrutils.getMessages()
				.contains("Please provide a scolomfr file before calling scolomfrutils methods."));
		assertFalse("Result of validation is valid", scolomfrutils.isValid());
	}
}
