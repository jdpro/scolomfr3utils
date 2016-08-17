package fr.apiscol.metadata.scolomfr3utils.command.check;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;

public class TaxonPathCheckCommand extends AbstractCommand {

	private final XPathFactory xpathFactory = XPathFactory.newInstance();

	private final XPath xPath = xpathFactory.newXPath();

	private Document scolomfrDocument;

	private static DocumentBuilder domDocumentBuilder;

	@Override
	public void execute() throws CommandFailureException {
		try {
			if (domDocumentBuilder == null) {
				domDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			}
			scolomfrDocument = domDocumentBuilder.parse(getScolomfrFile());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
		List<List<String>> taxonIdsLists = null;
		try {
			taxonIdsLists = getTaxonLists();

		} catch (XPathExpressionException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
		List<String> taxonIdList = null;
		;
		for (int i = 0; i < taxonIdsLists.size(); i++) {
			taxonIdList = taxonIdsLists.get(i);
			checkTaxonAreConsecutive(taxonIdList);
		}
	}

	private void checkTaxonAreConsecutive(List<String> taxonIdsList) throws CommandFailureException {
		Iterator<String> it = taxonIdsList.iterator();
		String previousTaxonId = null;
		ArrayList<String> messages = new ArrayList<>();
		while (it.hasNext()) {
			String taxonId = (String) it.next();
			if (!StringUtils.isEmpty(previousTaxonId)) {
				if (!getSkosApi().isBroaderThan(previousTaxonId, taxonId)) {
					String taxonPreflabel = getSkosApi().getPrefLabelForResource(taxonId);
					String previousTaxonPreflabel = getSkosApi().getPrefLabelForResource(previousTaxonId);
					messages.add(String.format(
							"Taxon %s (%s) follows taxon %s (%s) but the latter is not connected to the former by a broader relation",
							taxonId, taxonPreflabel, previousTaxonId, previousTaxonPreflabel));
				}
			}
			previousTaxonId = taxonId;
		}
		if (!messages.isEmpty()) {
			throw new CommandFailureException(messages);
		}
	}

	private List<List<String>> getTaxonLists() throws XPathExpressionException {
		List<List<String>> taxonLists = new ArrayList<>();
		NodeList taxonPathNodes = null;
		taxonPathNodes = (NodeList) xPath.evaluate("/lom/classification/taxonPath", scolomfrDocument,
				XPathConstants.NODESET);
		Node taxonPathNode = null;
		NodeList taxonNodeIds = null;
		for (int i = 0; i < taxonPathNodes.getLength(); i++) {
			List<String> taxonList = new LinkedList<>();
			taxonPathNode = taxonPathNodes.item(i);
			taxonNodeIds = (NodeList) xPath.evaluate("taxon/id", taxonPathNode, XPathConstants.NODESET);
			if (taxonNodeIds.getLength() == 0) {
				continue;
			}
			Node taxonNodeId = null;
			for (int j = 0; j < taxonNodeIds.getLength(); j++) {
				taxonNodeId = taxonNodeIds.item(j);
				taxonList.add(taxonNodeId.getTextContent().trim());
			}
			taxonLists.add(taxonList);
		}
		return taxonLists;
	}

	@Override
	public boolean isXsdRequired() {
		return false;
	}

	@Override
	public boolean isSkosRequired() {
		return true;
	}

	@Override
	public boolean isScolomfrFileRequired() {
		return true;
	}

	

}
