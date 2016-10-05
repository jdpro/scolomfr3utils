package fr.apiscol.metadata.scolomfr3utils.command.check.label;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosLoader;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

/**
 * Test that consecutive taxons refers to entries related by broder/narrower in
 * Skos file
 */
public class LabelCheckCommandTest {

	private static final String INVALID_LABEL_COURS_FAILURE_MESSAGE = String
			.format(LabelCheckCommand.RESOURCE_LABEL_DOES_NOT_MATCH_ANY_LABEL_OF_URI, "142", "cours", "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-008-num-03");
	private static final String INVALID_LABEL_QUOTE_FAILURE_MESSAGE = String
			.format(LabelCheckCommand.RESOURCE_LABEL_DOES_NOT_MATCH_ANY_LABEL_OF_URI, "515", "logiciel organiseur d’idées", "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-020-num-057");
	private AbstractCommand taxonPathVocabCheckCommand;

	@Before
	public void setup() {
		taxonPathVocabCheckCommand = new LabelCheckCommand();
		Model skosModel = new SkosLoader().loadSkos(new SchemaVersion(3, 0));
		ISkosApi skosApi = new SkosApi();
		skosApi.setSkosModel(skosModel);
		taxonPathVocabCheckCommand.setSkosApi(skosApi);
	}

	@Test
	public void testValidationSuccessWithRightLabels() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/labels/valid/exemple.xml");
		taxonPathVocabCheckCommand
				.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = taxonPathVocabCheckCommand.execute();
		assertTrue("TaxonPath check command should not have failed.", result);
		List<String> messages = taxonPathVocabCheckCommand.getMessages();
		assertTrue("The validation messages list should be empty", messages.isEmpty());

	}

	@Test
	public void testValidationFailureWithWrongLabels() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/labels/invalid/exemple.xml");
		taxonPathVocabCheckCommand
				.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = taxonPathVocabCheckCommand.execute();
		assertFalse("TaxonPath check command should have failed.", result);
		List<String> failureMessages = taxonPathVocabCheckCommand.getMessages(MessageStatus.FAILURE);
		assertTrue("The validation messages should contain : " + INVALID_LABEL_COURS_FAILURE_MESSAGE,
				failureMessages.contains(INVALID_LABEL_COURS_FAILURE_MESSAGE));
		assertTrue("The validation messages should contain : " + INVALID_LABEL_QUOTE_FAILURE_MESSAGE,
				failureMessages.contains(INVALID_LABEL_QUOTE_FAILURE_MESSAGE));
	}

}
