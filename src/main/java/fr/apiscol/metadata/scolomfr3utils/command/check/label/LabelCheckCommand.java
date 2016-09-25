package fr.apiscol.metadata.scolomfr3utils.command.check.label;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.IScolomfrDomDocumentRequired;
import fr.apiscol.metadata.scolomfr3utils.command.ISkosApiRequired;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

public class LabelCheckCommand extends AbstractCommand implements IScolomfrDomDocumentRequired, ISkosApiRequired {

	static final String RESOURCE_LABEL_DOES_NOT_MATCH_ANY_LABEL_OF_URI = "Resource label \"%s\" line %s does not match any label of uri \"%s\"";

	@Override
	public boolean execute() {
		List<Pair<Node, Node>> labelValuePairs = getLabelvaluePairs();
		Iterator<Pair<Node, Node>> it = labelValuePairs.iterator();
		boolean valid = true;
		while (it.hasNext()) {
			Pair<Node, Node> pair = it.next();
			boolean matches = labelMatchesValue(pair);
			valid = valid && matches;
		}

		return valid;
	}

	private boolean labelMatchesValue(Pair<Node, Node> pair) {
		String resourceUri = pair.getLeft().getTextContent().trim();
		String resourceLabel = pair.getRight().getTextContent().trim();
		boolean resourceHasLabel = getSkosApi().resourceHasLabel(resourceUri, resourceLabel);
		if (!resourceHasLabel) {
			addMessage(MessageStatus.FAILURE,
					String.format(RESOURCE_LABEL_DOES_NOT_MATCH_ANY_LABEL_OF_URI, resourceLabel,
							pair.getRight().getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY),
							resourceUri));
			return false;
		}
		return true;
	}

	private List<Pair<Node, Node>> getLabelvaluePairs() {
		List<Pair<Node, Node>> pairs = new ArrayList<>();
		NodeList labelValueParents = null;
		try {
			labelValueParents = (NodeList) xPath.evaluate("//*[label and value]", scolomfrDocument,
					XPathConstants.NODESET);
			for (int i = 0; i < labelValueParents.getLength(); i++) {
				Node labelValueParent = labelValueParents.item(i);
				Node label = (Node) xPath.evaluate("label", labelValueParent, XPathConstants.NODE);
				Node value = (Node) xPath.evaluate("value", labelValueParent, XPathConstants.NODE);
				Pair<Node, Node> pair = new ImmutablePair<>(value, label);
				pairs.add(pair);
			}
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, e.getMessage());
		}
		return pairs;
	}

}
