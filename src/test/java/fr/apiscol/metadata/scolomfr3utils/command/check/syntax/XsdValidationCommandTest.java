package fr.apiscol.metadata.scolomfr3utils.command.check.syntax;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;
import fr.apiscol.metadata.scolomfr3utils.xsd.ValidatorLoader;

/**
 * Test xsd validation with different kinds of files
 */
public class XsdValidationCommandTest {
	public static final String DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE = "# Global : cvc-identity-constraint.4.1: Duplicate unique value [general] declared for identity constraint \"lomUnique\" of element \"lom\".";

	private XsdValidationCommand xsdValidationCommand;

	@Before
	public void setup() {
		xsdValidationCommand = new XsdValidationCommand();
		xsdValidationCommand.setXsdValidator(new ValidatorLoader().loadXsd(new SchemaVersion(3, 0)));
	}

	@Test
	public void testXsdValidationSuccess() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/any/valid/exemple.xml");
		xsdValidationCommand.setScolomfrFile(scolomfrFile);
		boolean result = xsdValidationCommand.execute();
		assertTrue("Classification purpose check command should be successful.", result);
	}

	@Test
	public void testXsdValidationFailure() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/1/invalid/double-general.xml");
		xsdValidationCommand.setScolomfrFile(scolomfrFile);
		boolean result = xsdValidationCommand.execute();
		assertFalse("Classification purpose check command should have failed.", result);
		List<String> failureMessages = xsdValidationCommand.getMessages(MessageStatus.FAILURE);
		assertTrue("The validation messages should contain : " + DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE,
				failureMessages.contains(DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE));

	}

}
