package fr.apiscol.metadata.scolomfr3utils.command.check;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;

public class ClassificationPurposesCheckCommand extends AbstractCommand {
	@Override
	public void execute() throws CommandFailureException {
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

	private void checkTaxonPathsAreInTheRightPurpose(final Node classificationNode) throws CommandFailureException {
		String purpose = getClassificationPurpose(classificationNode);
		
	}

	private String getClassificationPurpose(final Node classificationNode) throws CommandFailureException {
		Node purposeNode;
		try {
			purposeNode = (Node) xPath.evaluate("purpose/value", scolomfrDocument, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
		if (purposeNode == null) {
			throw new CommandFailureException("This classification node has no associated purpose");
		}
		return purposeNode.getTextContent();
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
