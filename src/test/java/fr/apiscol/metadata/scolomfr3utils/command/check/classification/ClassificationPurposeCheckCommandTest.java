package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.VersionHandlingException;
import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosLoader;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

/**
 * Test that each vocabulary is located under the right purpose
 */
public class ClassificationPurposeCheckCommandTest {

	private static final String VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE = String.format(
			ClassificationPurposesCheckCommand.VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE_PATTERN, "34",
			"http://data.education.fr/voc/scolomfr/scolomfr-voc-015",
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-003");
	private static final String INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE = String.format(
			ClassificationPurposesCheckCommand.INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE_PATTERN,
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-005-num-007", "110");;
	private AbstractCommand classificationPurposesCheckCommand;

	@Before
	public void setup() {
		classificationPurposesCheckCommand = new ClassificationPurposesCheckCommand();
		Model skosModel = new SkosLoader().loadSkos(new SchemaVersion(3, 0));
		ISkosApi skosApi = new SkosApi();
		skosApi.setSkosModel(skosModel);
		classificationPurposesCheckCommand.setSkosApi(skosApi);

	}

	@Test
	public void testValidationFailureWithWrongPurpose() throws Exception {
		classificationPurposesCheckCommand.setScolomfrVersion(new SchemaVersion(3, 0));
		File scolomfrFile = new File("src/test/data/3.0/9/invalid/classification-with-wrong-purpose.xml");
		classificationPurposesCheckCommand
				.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = classificationPurposesCheckCommand.execute();
		assertFalse("Classification purpose check command should have failed.", result);
		List<String> failureMessages = classificationPurposesCheckCommand.getMessages(MessageStatus.FAILURE);
		assertTrue("The validation messages should contain : " + VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE,
				failureMessages.contains(VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE));

	}

	@Test
	public void testValidationFailureWithInvalidResourceAsVocabulray() throws Exception {
		classificationPurposesCheckCommand.setScolomfrVersion(new SchemaVersion(3, 0));
		File scolomfrFile = new File(
				"src/test/data/3.0/9/invalid/classification-with-invalid-resource-as-vocabulary.xml");
		classificationPurposesCheckCommand
				.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = classificationPurposesCheckCommand.execute();
		assertFalse("Classification purpose check command should have failed.", result);
		List<String> failureMessages = classificationPurposesCheckCommand.getMessages(MessageStatus.FAILURE);
		assertTrue("The validation messages should contain : " + INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE,
				failureMessages.contains(INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE));

	}

	@Test
	public void testValidationFailureWithoutScolomfrVersion() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/classification-with-right-purpose.xml");
		classificationPurposesCheckCommand
				.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = classificationPurposesCheckCommand.execute();
		assertFalse("Classification purpose check command should have failed.", result);
		List<String> failureMessages = classificationPurposesCheckCommand.getMessages(MessageStatus.FAILURE);
		assertTrue(
				"The validation messages should contain : "
						+ ClassificationPurposesCheckCommand.MISSING_SCO_LO_MFR_SCHEMA_VERSION_MESSAGE,
				failureMessages.contains(ClassificationPurposesCheckCommand.MISSING_SCO_LO_MFR_SCHEMA_VERSION_MESSAGE));

	}

	@Test(expected = VersionHandlingException.class)
	public void testValidationFailureWithNotSupportedScolomfrVersion() throws Exception {
		ClassificationPurposeAndVocapularyMatcherFactory factory = new ClassificationPurposeAndVocapularyMatcherFactory();
		factory.getMatcher("2.1");

	}

}
