package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.TaxonPathCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosLoader;

/**
 * Test that consecutive taxons refers to entries related by broder/narrower in
 * Skos file
 */
public class TaxonPathCheckCommandTest {

	private static final String NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE = String.format(
			TaxonPathCheckCommand.NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE_PATTERN,
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-022-num-020", "5e", "53",
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-022-num-021", "4e");
	private AbstractCommand taxonPathCheckCommand;

	@Before
	public void setup() {
		taxonPathCheckCommand = new TaxonPathCheckCommand();
		Model skosModel = new SkosLoader().loadSkos("3.0");
		ISkosApi skosApi = new SkosApi();
		skosApi.setSkosModel(skosModel);
		taxonPathCheckCommand.setSkosApi(skosApi);
	}

	@Test
	public void testValidationSuccessWithConsecutiveTaxons() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/consecutive-taxons.xml");
		taxonPathCheckCommand.setScolomfrFile(scolomfrFile);
		taxonPathCheckCommand.execute();

	}

	@Test
	public void testValidationSuccessWithUnknownTaxons() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/valid/unknown-taxons.xml");
		taxonPathCheckCommand.setScolomfrFile(scolomfrFile);
		taxonPathCheckCommand.execute();

	}

	@Test(expected = CommandFailureException.class)
	public void testValidationFailureWithNonConsecutiveTaxons() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/invalid/non-consecutive-taxons.xml");
		taxonPathCheckCommand.setScolomfrFile(scolomfrFile);
		try {
			taxonPathCheckCommand.execute();
		} catch (CommandFailureException e) {
			assertTrue("The validation messages should contain : " + NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE,
					e.getMessages().contains(NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE));
			throw (e);
		}
	}

}
