package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

public class TaxonPathCheckCommand extends AbstractCommand {

	static final String NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE_PATTERN = "Taxon %s (%s) line %s follows taxon %s (%s) but the latter is not connected to the former by a broader relation";

	@Override
	public boolean execute() {
		List<List<Node>> taxonNodesLists = null;
		try {
			taxonNodesLists = getTaxonLists();

		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, e.getMessage());
			return false;
		}
		List<Node> taxonNodesList = null;
		boolean valid = true;
		for (int i = 0; i < taxonNodesLists.size(); i++) {
			taxonNodesList = taxonNodesLists.get(i);
			valid &= checkTaxonAreConsecutive(taxonNodesList);
		}
		return valid;
	}

	private boolean checkTaxonAreConsecutive(List<Node> taxonNodesList) {
		Iterator<Node> it = taxonNodesList.iterator();
		String previousTaxonIdUri = null;
		String taxonIdUri;
		boolean valid = true;
		while (it.hasNext()) {
			Node taxonId = (Node) it.next();
			taxonIdUri = taxonId.getTextContent().trim();
			if (!StringUtils.isEmpty(previousTaxonIdUri)) {
				if (getSkosApi().resourceExists(taxonIdUri) && getSkosApi().resourceExists(previousTaxonIdUri)
						&& !getSkosApi().isBroaderThan(previousTaxonIdUri, taxonIdUri)) {
					String taxonPreflabel = getSkosApi().getPrefLabelForResource(taxonIdUri);
					String previousTaxonPreflabel = getSkosApi().getPrefLabelForResource(previousTaxonIdUri);
					String lineNumber = taxonId.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)
							.toString();
					addMessage(MessageStatus.FAILURE, String.format(NON_CONSECUTIVE_TAXONS_FAILURE_MESSAGE_PATTERN,
							taxonIdUri, taxonPreflabel, lineNumber, previousTaxonIdUri, previousTaxonPreflabel));
					valid = false;
				}
			}
			previousTaxonIdUri = taxonIdUri;
		}
		return valid;
	}

	private List<List<Node>> getTaxonLists() throws XPathExpressionException {
		List<List<Node>> taxonLists = new ArrayList<>();
		NodeList taxonPathNodes = null;
		taxonPathNodes = (NodeList) xPath.evaluate("/lom/classification/taxonPath", scolomfrDocument,
				XPathConstants.NODESET);
		Node taxonPathNode = null;
		NodeList taxonNodeIds = null;
		for (int i = 0; i < taxonPathNodes.getLength(); i++) {
			List<Node> taxonList = new LinkedList<>();
			taxonPathNode = taxonPathNodes.item(i);
			taxonNodeIds = (NodeList) xPath.evaluate("taxon/id", taxonPathNode, XPathConstants.NODESET);
			if (taxonNodeIds.getLength() == 0) {
				continue;
			}
			Node taxonNodeId = null;
			for (int j = 0; j < taxonNodeIds.getLength(); j++) {
				taxonNodeId = taxonNodeIds.item(j);
				taxonList.add(taxonNodeId);
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
		return false;
	}

	@Override
	public boolean isScolomfrDomDocumentRequired() {
		return true;
	}

}
