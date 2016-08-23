package fr.apiscol.metadata.scolomfr3utils.command.check;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;
import fr.apiscol.metadata.scolomfr3utils.xsd.ValidatorLoader;

/**
 * Test xsd validation with different kinds of files
 */
public class XsdValidationCommandTest {
	public static final String DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE = "cvc-identity-constraint.4.1: Duplicate unique value [general] declared for identity constraint \"lomUnique\" of element \"lom\".";

	private XsdValidationCommand xsdValidationCommand;

	@Before
	public void setup() {
		xsdValidationCommand = new XsdValidationCommand();
		xsdValidationCommand.setXsdValidator(new ValidatorLoader().loadXsd("3.0"));
	}

	@Test
	public void testXsdValidationSuccess() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/any/valid/exemple.xml");
		xsdValidationCommand.setScolomfrFile(scolomfrFile);
		xsdValidationCommand.execute();

	}

	@Test(expected = CommandFailureException.class)
	public void testXsdValidationFailure() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/1/invalid/double-general.xml");
		xsdValidationCommand.setScolomfrFile(scolomfrFile);
		try {
			xsdValidationCommand.execute();
		} catch (CommandFailureException e) {
			assertTrue("The validation messages should contain : " + DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE,
					e.getMessages().contains(DUPLICATE_GENERAL_ELEMENT_FAILURE_MESSAGE));
			throw (e);
		}
	}

}
