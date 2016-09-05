package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

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

/**
 * Test that consecutive taxons refers to entries related by broder/narrower in
 * Skos file
 */
public class TaxonPathVocabCheckCommandTest {

	private static final String MISSING_SOURCE_ELEMENT_MESSAGE = String
			.format(TaxonPathVocabCheckCommand.MISSING_SOURCE_ELEMENT_MESSAGE_TEMPLATE, "19");
	private static final String TAXON_DOES_NOT_BELONG_TO_VOCABULARY_FAILURE_MESSAGE = String.format(
			TaxonPathVocabCheckCommand.TAXON_DOES_NOT_BELONG_TO_VOCABULARY_MESSAGE_TEMPLATE,
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-022-num-626", "32",
			"http://data.education.fr/voc/scolomfr/scolomfr-voc-015");
	private AbstractCommand taxonPathVocabCheckCommand;

	@Before
	public void setup() {
		taxonPathVocabCheckCommand = new TaxonPathVocabCheckCommand();
		Model skosModel = new SkosLoader().loadSkos("3.0");
		ISkosApi skosApi = new SkosApi();
		skosApi.setSkosModel(skosModel);
		taxonPathVocabCheckCommand.setSkosApi(skosApi);
	}

	@Test
	public void testValidationSuccessWithRightVocabulary() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/taxon-member-of-vocabulary.xml");
		taxonPathVocabCheckCommand.setScolomfrFile(scolomfrFile);
		boolean result = taxonPathVocabCheckCommand.execute();
		assertTrue("TaxonPath check command should not have failed.", result);
		List<String> messages = taxonPathVocabCheckCommand.getMessages();
		assertTrue("The validation messages list should be empty", messages.isEmpty());

	}

	@Test
	public void testValidationSuccessWithTaxonPathWithoutSource() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/taxonpath-without-source.xml");
		taxonPathVocabCheckCommand.setScolomfrFile(scolomfrFile);
		boolean result = taxonPathVocabCheckCommand.execute();
		assertTrue("TaxonPath check command should not have failed.", result);
		List<String> messages = taxonPathVocabCheckCommand.getMessages(MessageStatus.WARNING);
		assertTrue("The validation messages list should contain : " + MISSING_SOURCE_ELEMENT_MESSAGE,
				messages.contains(MISSING_SOURCE_ELEMENT_MESSAGE));

	}

	@Test
	public void testValidationFailureWithWrongVocabulary() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/invalid/taxon-non-member-of-vocabulary.xml");
		taxonPathVocabCheckCommand.setScolomfrFile(scolomfrFile);
		boolean result = taxonPathVocabCheckCommand.execute();
		assertFalse("TaxonPath check command should have failed.", result);
		List<String> failureMessages = taxonPathVocabCheckCommand.getMessages(MessageStatus.FAILURE);
		assertTrue("The validation messages should contain : " + TAXON_DOES_NOT_BELONG_TO_VOCABULARY_FAILURE_MESSAGE,
				failureMessages.contains(TAXON_DOES_NOT_BELONG_TO_VOCABULARY_FAILURE_MESSAGE));
	}

}
