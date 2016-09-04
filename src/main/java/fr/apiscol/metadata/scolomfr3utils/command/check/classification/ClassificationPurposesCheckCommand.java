package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

public class ClassificationPurposesCheckCommand extends AbstractCommand {
	private static final String MISSING_SCO_LO_MFR_SCHEMA_VERSION_MESSAGE = "Please provide the ScoLOMfr schema version to check classification purposes.";
	static final String CLASSIFICATION_WITHOUT_PURPOSE_MESSAGE_PATTERN = "Classification element line %s node has no associated purpose";
	static final String VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE_PATTERN = "Invalid element source line %s : you can't use vocabulary %s under purpose %s";
	static final String INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE_PATTERN = "TaxonPath uses a skos resource %s that is not a vocabulary as source line %s";

	@Override
	public boolean execute() {
		if (StringUtils.isEmpty(getScolomfrVersion())) {
			addMessage(MessageStatus.FAILURE, MISSING_SCO_LO_MFR_SCHEMA_VERSION_MESSAGE);
			return false;
		}
		buildScolomfrDocument();
		NodeList classificationNodes = null;
		try {
			classificationNodes = (NodeList) xPath.evaluate("/lom/classification", scolomfrDocument,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, e.getMessage());
			return false;
		}
		boolean valid = true;
		for (int i = 0; i < classificationNodes.getLength(); i++) {
			Node classificationNode = classificationNodes.item(i);
			valid &= checkTaxonPathsAreInTheRightPurpose(classificationNode);
		}
		return valid;
	}

	private boolean checkTaxonPathsAreInTheRightPurpose(final Node classificationNode) {
		String purpose = getClassificationPurpose(classificationNode);
		String vocabularyId;
		try {
			vocabularyId = new ClassificationPurposeAndVocapularyMatcherFactory().getMatcher(getScolomfrVersion())
					.getVocabularyId(purpose);
		} catch (Exception e) {
			addMessage(MessageStatus.FAILURE, e.getMessage());
			return false;
		}
		if (null == vocabularyId) {
			// This purpose has no associated vocabulary, there's nothing to
			// check
			return true;
		}
		NodeList taxonPathSources = getTaxonPathSourceNodes(classificationNode);
		if (null == taxonPathSources) {
			// Impossible to get taxonpath source
			// There's nothing to check
			return true;
		}
		Node taxonPathSource;
		String taxonPathSourceId;
		// TODO : warnings for taxonpaths without sources
		boolean valid = true;
		for (int i = 0; i < taxonPathSources.getLength(); i++) {
			taxonPathSource = taxonPathSources.item(i);
			taxonPathSourceId = taxonPathSource.getTextContent().trim();
			if (!getSkosApi().vocabularyExists(taxonPathSourceId)) {
				if (getSkosApi().resourceExists(taxonPathSourceId)) {
					addMessage(MessageStatus.FAILURE,
							String.format(INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE_PATTERN, taxonPathSourceId,
									taxonPathSource.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
					valid = false;
				}
				// This vocabulary doesn't belong to official vocabularies
				// referenced in Skos file
				// You can index your resource with custom vocabularies
				// under any purpose
				continue;
			}
			if (!StringUtils.equalsIgnoreCase(taxonPathSourceId, vocabularyId)) {
				addMessage(MessageStatus.FAILURE,
						String.format(VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE_PATTERN,
								taxonPathSource.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY),
								vocabularyId, purpose));
				valid = false;
			}
		}
		return valid;
	}

	private NodeList getTaxonPathSourceNodes(Node classificationNode) {
		NodeList sourceNodes = null;
		try {
			sourceNodes = (NodeList) xPath.evaluate("taxonPath/source/string", classificationNode,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.WARNING, e.getMessage());
			return null;
		}
		return sourceNodes;
	}

	private String getClassificationPurpose(final Node classificationNode) {
		Node purposeNode;
		try {
			purposeNode = (Node) xPath.evaluate("purpose/value", classificationNode, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.WARNING, e.getMessage());
			return null;
		}
		if (purposeNode == null) {
			addMessage(MessageStatus.WARNING, String.format(CLASSIFICATION_WITHOUT_PURPOSE_MESSAGE_PATTERN,
					classificationNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
			return null;
		}
		return purposeNode.getTextContent().trim();
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