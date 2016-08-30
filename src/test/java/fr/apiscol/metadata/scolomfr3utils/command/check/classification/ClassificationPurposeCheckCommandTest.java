package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosLoader;

/**
 * Test that each vocabulary is located under the right purpose
 */
public class ClassificationPurposeCheckCommandTest {

	private static final String VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE = String.format(
			ClassificationPurposesCheckCommand.VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE_PATTERN, "31",
			"http://data.education.fr/voc/scolomfr/scolomfr-voc-015",
			"http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-003");
	private AbstractCommand classificationPurposesCheckCommand;

	@Before
	public void setup() {
		classificationPurposesCheckCommand = new ClassificationPurposesCheckCommand();
		Model skosModel = new SkosLoader().loadSkos("3.0");
		ISkosApi skosApi = new SkosApi();
		skosApi.setSkosModel(skosModel);
		classificationPurposesCheckCommand.setSkosApi(skosApi);
		classificationPurposesCheckCommand.setScolomfrVersion("3.0");
	}

	@Test(expected = CommandFailureException.class)
	public void testValidationFailureWithWrongPurpose() throws Exception {
		File scolomfrFile = new File("src/test/data/3.0/9/invalid/classification-with-wrong-purpose.xml");
		classificationPurposesCheckCommand.setScolomfrFile(scolomfrFile);
		try {
			classificationPurposesCheckCommand.execute();
		} catch (CommandFailureException e) {
			List<String> mes = e.getMessages();
			Iterator<String> it = mes.iterator();
			while (it.hasNext()) {
				String string = (String) it.next();
				System.out.println(string);
			}
			assertTrue("The validation messages should contain : " + VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE,
					e.getMessages().contains(VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE));
			throw (e);
		}
	}

	

}
