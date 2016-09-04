package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.CommandException;
import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;
import fr.apiscol.metadata.scolomfr3utils.command.CommandWarningException;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

public class TaxonPathVocabCheckCommand extends AbstractCommand {

	private static final String TAXON_DOES_NOT_BELONG_TO_VOCABULARY_MESSAGE_TEMPLATE = "Taxon %s line %s does not belong to vocabulary %s";
	private static final String MISSING_SOURCE_ELEMENT_MESSAGE_TEMPLATE = "Classification node line %s is missing a source child élément.";
	private ArrayList<String> warningMessages;
	private ArrayList<String> failureMessages;

	@Override
	public void execute() throws CommandException {
		buildScolomfrDocument();
		warningMessages = new ArrayList<>();
		failureMessages = new ArrayList<>();
		Map<String, List<Node>> taxonNodesListsBySource = null;
		try {
			taxonNodesListsBySource = getTaxonNodesListsBySource();

		} catch (XPathExpressionException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
		Iterator<String> it = taxonNodesListsBySource.keySet().iterator();
		while (it.hasNext()) {
			String vocabUri = (String) it.next();
			if (!getSkosApi().vocabularyExists(vocabUri)) {
				continue;
			}
			checkTaxonsBelongToVocabulary(vocabUri, taxonNodesListsBySource.get(vocabUri));
		}
		if (!failureMessages.isEmpty()) {
			throw new CommandFailureException(failureMessages);
		}
		if (!warningMessages.isEmpty()) {
			throw new CommandWarningException(failureMessages);
		}
	}

	private void checkTaxonsBelongToVocabulary(String vocabUri, List<Node> taxonNodesList)
			throws CommandFailureException {
		Node taxonNode;
		for (int i = 0; i < taxonNodesList.size(); i++) {
			taxonNode = taxonNodesList.get(i);
			String taxonuri = taxonNode.getTextContent().trim();
			if (!getSkosApi().resourceIsMemberOfVocabulary(taxonuri, vocabUri)) {
				failureMessages.add(String.format(TAXON_DOES_NOT_BELONG_TO_VOCABULARY_MESSAGE_TEMPLATE, taxonuri,
						taxonNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY), vocabUri));
			}
		}

	}

	private Map<String, List<Node>> getTaxonNodesListsBySource() throws XPathExpressionException {
		Map<String, List<Node>> taxonLists = new HashMap<>();
		NodeList classificationNodes = null;

		classificationNodes = (NodeList) xPath.evaluate("/lom/classification", scolomfrDocument,
				XPathConstants.NODESET);
		Node classificationNode = null;
		NodeList taxonNodeIds = null;
		for (int i = 0; i < classificationNodes.getLength(); i++) {
			classificationNode = classificationNodes.item(i);
			Node taxonPathSourceNode = (Node) xPath.evaluate("taxonPath/source/string", classificationNode,
					XPathConstants.NODE);
			if (null == taxonPathSourceNode) {
				warningMessages.add(String.format(MISSING_SOURCE_ELEMENT_MESSAGE_TEMPLATE,
						classificationNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
				continue;
			}
			String taxonPathSourceId = taxonPathSourceNode.getTextContent().trim();
			if (StringUtils.isEmpty(taxonPathSourceId)) {
				warningMessages.add(String.format(MISSING_SOURCE_ELEMENT_MESSAGE_TEMPLATE,
						taxonPathSourceNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
				continue;
			}
			List<Node> taxonList = new LinkedList<>();

			taxonNodeIds = (NodeList) xPath.evaluate("taxonPath/taxon/id", classificationNode, XPathConstants.NODESET);
			if (taxonNodeIds.getLength() == 0) {
				continue;
			}
			Node taxonNodeId = null;
			for (int j = 0; j < taxonNodeIds.getLength(); j++) {
				taxonNodeId = taxonNodeIds.item(j);
				taxonList.add(taxonNodeId);
			}
			taxonLists.put(taxonPathSourceId, taxonList);
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
