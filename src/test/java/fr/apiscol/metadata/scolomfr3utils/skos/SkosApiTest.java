package fr.apiscol.metadata.scolomfr3utils.skos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosLoader;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

/**
 * Test that each vocabulary is located under the right purpose
 */
public class SkosApiTest {

	private ISkosApi skosApi;

	@Before
	public void setup() {
		Model skosModel = new SkosLoader().loadSkos("3.0");
		skosApi = new SkosApi();
		skosApi.setSkosModel(skosModel);
	}

	@Test
	public void testNoPrefLabelForMissingressource() throws Exception {
		String result = skosApi.getPrefLabelForResource("http://www.example.com/any-uri");
		assertTrue("PrefLabel should be" + SkosApi.NO_RESULT, StringUtils.equals(SkosApi.NO_RESULT, result));

	}

}
