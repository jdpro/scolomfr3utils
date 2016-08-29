package fr.apiscol.metadata.scolomfr3utils.command.check;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.CommandException;
import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;
import fr.apiscol.metadata.scolomfr3utils.command.CommandWarningException;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.ClassificationPurposeAndVocapularyMatcherFactory;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

public class ClassificationPurposesCheckCommand extends AbstractCommand {
	private static final String CLASSIFICATION_WITHOUT_PURPOSE_MESSAGE_PATTERN = "Classification element line %s node has no associated purpose";
	private static final String VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE_PATTERN = "Invalid element source line %s : you can't use vocabulary %s under purpose %s";
	private static final String INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE_PATTERN = "TaxonPath uses a skos resource %s that is not a vocabulary as source line %s";

	@Override
	public void execute() throws CommandException {
		buildScolomfrDocument();
		NodeList classificationNodes = null;
		try {
			classificationNodes = (NodeList) xPath.evaluate("/lom/classification", scolomfrDocument,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
		for (int i = 0; i < classificationNodes.getLength(); i++) {
			Node classificationNode = classificationNodes.item(i);
			checkTaxonPathsAreInTheRightPurpose(classificationNode);
		}
	}

	private void checkTaxonPathsAreInTheRightPurpose(final Node classificationNode) throws CommandException {
		String purpose = getClassificationPurpose(classificationNode);
		try {
			String vocabularyId = new ClassificationPurposeAndVocapularyMatcherFactory()
					.getMatcher(getScolomfrVersion()).getVocabularyId(purpose);
			if (null == vocabularyId) {
				// This purpose has no associated vocabulary, there's nothing to
				// check
				return;
			}
			NodeList taxonPathSources = getTaxonPathSourceNodes(classificationNode);
			Node taxonPathSource;
			String taxonPathSourceId;
			// TODO : warnings for taxonpaths without sources
			for (int i = 0; i < taxonPathSources.getLength(); i++) {
				taxonPathSource = taxonPathSources.item(i);
				taxonPathSourceId = taxonPathSource.getTextContent().trim();
				if (!getSkosApi().vocabularyExists(taxonPathSourceId)) {
					if (getSkosApi().resourceExists(taxonPathSourceId)) {
						throw new CommandFailureException(String.format(
								INVALID_RESOURCE_USED_AS_VOCABULARY_MESSAGE_PATTERN, taxonPathSourceId,
								taxonPathSource.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
					}
					// This vocabulary doesn't belong to official vocabularies
					// referenced in Skos file
					// You can index your resource with custom vocabularies
					// under any purpose
					return;
				}
				if (!StringUtils.equalsIgnoreCase(taxonPathSourceId, vocabularyId)) {
					throw new CommandFailureException(
							String.format(VOCABULARY_NOT_ALLOWED_UNDER_PURPOSE_MESSAGE_PATTERN,
									taxonPathSource.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY),
									vocabularyId, purpose));
				}
			}
		} catch (Exception e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
	}

	private NodeList getTaxonPathSourceNodes(Node classificationNode) throws CommandException {
		NodeList sourceNodes = null;
		try {
			sourceNodes = (NodeList) xPath.evaluate("taxonPath/source/string", classificationNode,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
		return sourceNodes;
	}

	private String getClassificationPurpose(final Node classificationNode) throws CommandException {
		Node purposeNode;
		try {
			purposeNode = (Node) xPath.evaluate("purpose/value", classificationNode, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
		if (purposeNode == null) {
			throw new CommandWarningException(String.format(CLASSIFICATION_WITHOUT_PURPOSE_MESSAGE_PATTERN,
					classificationNode.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));

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
