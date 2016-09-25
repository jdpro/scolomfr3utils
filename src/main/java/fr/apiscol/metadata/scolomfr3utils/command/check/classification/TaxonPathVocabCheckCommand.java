package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import java.util.Collections;
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
import fr.apiscol.metadata.scolomfr3utils.command.IScolomfrDomDocumentRequired;
import fr.apiscol.metadata.scolomfr3utils.command.ISkosApiRequired;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

/**
 * Checks that all taxons listed in taxonPaths belong to the source vocabulary
 * of their taxonPath. If the vocabulary is a custom one (i.e. not described by
 * scolomfr skos file) nothing happens. If source is not specified, a warning is
 * emitted.
 */
public class TaxonPathVocabCheckCommand extends AbstractCommand
		implements IScolomfrDomDocumentRequired, ISkosApiRequired {

	static final String TAXON_DOES_NOT_BELONG_TO_VOCABULARY_MESSAGE_TEMPLATE = "Taxon %s line \"%s\" does not belong to vocabulary \"%s\"";
	static final String MISSING_SOURCE_ELEMENT_MESSAGE_TEMPLATE = "Classification node line %s is missing a source child élément.";
	static final String VOCAB_URI_DOES_NOT_MATCH_TAXONS = "Vocabulary \"%s\" line %s does not match vocabulary of taxons used in classification : it should be URI \"%s\" or label \"%s\".";

	@Override
	public boolean execute() {
		Map<String, List<Node>> taxonNodesListsBySource = null;
		try {
			taxonNodesListsBySource = getTaxonNodesListsBySource();

		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, e.getMessage());
			return false;
		}
		Iterator<String> it = taxonNodesListsBySource.keySet().iterator();
		boolean valid = true;
		while (it.hasNext()) {
			String vocabLabelOrUri = it.next();
			String vocabUri;
			if (getSkosApi().vocabularyExists(vocabLabelOrUri)) {
				vocabUri = vocabLabelOrUri;
			} else {
				vocabUri = getSkosApi().getVocabUriByLabel(vocabLabelOrUri);
			}
			if (!StringUtils.isEmpty(vocabUri)) {
				valid &= checkTaxonsBelongToVocabulary(vocabUri, taxonNodesListsBySource.get(vocabLabelOrUri));
			} else {
				String taxonsCommonVocaburi = getSkosApi()
						.detectCommonVocabularyForTaxonNodes(taxonNodesListsBySource.get(vocabLabelOrUri));
				if (taxonsCommonVocaburi != null && !StringUtils.equalsIgnoreCase(vocabLabelOrUri, taxonsCommonVocaburi)
						&& !getSkosApi().resourceHasLabel(taxonsCommonVocaburi, vocabLabelOrUri)) {
					addMessage(MessageStatus.FAILURE,
							String.format(VOCAB_URI_DOES_NOT_MATCH_TAXONS, vocabLabelOrUri,
									getTaxonSourceNodeLineNumber(vocabLabelOrUri), taxonsCommonVocaburi,
									getSkosApi().getPrefLabelForResource(taxonsCommonVocaburi)));
					valid = false;
				}
			}

		}
		return valid;
	}

	private boolean checkTaxonsBelongToVocabulary(String vocabUri, List<Node> taxonNodesList) {
		Node taxonNode;
		boolean valid = true;
		for (int i = 0; i < taxonNodesList.size(); i++) {
			taxonNode = taxonNodesList.get(i);
			String taxonuri = taxonNode.getTextContent().trim();
			if (!getSkosApi().resourceIsMemberOfVocabulary(taxonuri, vocabUri)) {
				addMessage(MessageStatus.FAILURE, String.format(TAXON_DOES_NOT_BELONG_TO_VOCABULARY_MESSAGE_TEMPLATE,
						taxonuri, taxonNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY), vocabUri));
				valid = false;
			}
		}
		return valid;
	}

	private Map<String, List<Node>> getTaxonNodesListsBySource() throws XPathExpressionException {
		Map<String, List<Node>> taxonLists = new HashMap<>();
		NodeList classificationNodes;

		classificationNodes = (NodeList) xPath.evaluate("/lom/classification", scolomfrDocument,
				XPathConstants.NODESET);
		Node classificationNode;

		for (int i = 0; i < classificationNodes.getLength(); i++) {
			classificationNode = classificationNodes.item(i);
			Node taxonPathSourceNode = (Node) xPath.evaluate("taxonPath/source/string", classificationNode,
					XPathConstants.NODE);
			if (null == taxonPathSourceNode) {
				addMessage(MessageStatus.WARNING, String.format(MISSING_SOURCE_ELEMENT_MESSAGE_TEMPLATE,
						classificationNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
			} else {
				String taxonPathSourceId = taxonPathSourceNode.getTextContent().trim();
				if (StringUtils.isEmpty(taxonPathSourceId)) {
					addMessage(MessageStatus.WARNING, String.format(MISSING_SOURCE_ELEMENT_MESSAGE_TEMPLATE,
							taxonPathSourceNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
					continue;
				} else if (null == taxonLists.get(taxonPathSourceId)) {
					taxonLists.put(taxonPathSourceId, new LinkedList<Node>());
				}
				taxonLists.get(taxonPathSourceId).addAll(extractTaxons(classificationNode));
			}

		}
		return taxonLists;

	}

	private String getTaxonSourceNodeLineNumber(String taxonSource) {

		NodeList taxonPathSourceNodes;
		try {
			taxonPathSourceNodes = (NodeList) xPath.evaluate("/lom/classification/taxonPath/source/string",
					scolomfrDocument, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			return "";
		}

		for (int i = 0; i < taxonPathSourceNodes.getLength(); i++) {
			Node taxonPathSourceNode = taxonPathSourceNodes.item(i);
			String taxonPathSourceId = taxonPathSourceNode.getTextContent().trim();
			if (StringUtils.equalsIgnoreCase(taxonPathSourceId, taxonSource)) {
				return taxonPathSourceNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY).toString();
			}
		}
		return "";
	}

	private List<Node> extractTaxons(Node classificationNode) {
		List<Node> taxonList = new LinkedList<>();
		NodeList taxonNodeIds = null;
		try {
			taxonNodeIds = (NodeList) xPath.evaluate("taxonPath/taxon/id", classificationNode, XPathConstants.NODESET);
			Node taxonNodeId;
			for (int j = 0; j < taxonNodeIds.getLength(); j++) {
				taxonNodeId = taxonNodeIds.item(j);
				taxonList.add(taxonNodeId);
			}
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.WARNING, "Error while getting the list ot taxons ids : " + e.getMessage());
			return Collections.emptyList();
		}

		return taxonList;
	}

}
