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
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

/**
 * Test that consecutive taxons refers to entries related by broder/narrower in
 * Skos file
 */
public class TaxonPathCheckCommandTest {

	private static final String NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE = String.format(
			TaxonPathCheckCommand.NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE_PATTERN, "53",
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-022-num-020", "5e",
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-022-num-021", "4e");
	private AbstractCommand taxonPathCheckCommand;

	@Before
	public void setup() {
		taxonPathCheckCommand = new TaxonPathCheckCommand();
		Model skosModel = new SkosLoader().loadSkos(new SchemaVersion(3, 0));
		ISkosApi skosApi = new SkosApi();
		skosApi.setSkosModel(skosModel);
		taxonPathCheckCommand.setSkosApi(skosApi);
	}

	@Test
	public void testValidationSuccessWithConsecutiveTaxons() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/consecutive-taxons.xml");
		taxonPathCheckCommand.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = taxonPathCheckCommand.execute();
		assertTrue("Classification purpose check command should be successful.", result);

	}

	@Test
	public void testValidationSuccessWithUnknownTaxons() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/unknown-taxons.xml");
		taxonPathCheckCommand.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = taxonPathCheckCommand.execute();
		assertTrue("Taxonpaths check command should be successful.", result);
	}

	@Test
	public void testValidationFailureWithNonConsecutiveTaxons() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/invalid/non-consecutive-taxons.xml");
		taxonPathCheckCommand.setScolomfrDocument(DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile));
		boolean result = taxonPathCheckCommand.execute();
		assertFalse("TaxonPath check command should have failed.", result);
		List<String> failureMessages = taxonPathCheckCommand.getMessages(MessageStatus.FAILURE);
		assertTrue("The validation messages should contain : " + NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE,
				failureMessages.contains(NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE));

	}

}
