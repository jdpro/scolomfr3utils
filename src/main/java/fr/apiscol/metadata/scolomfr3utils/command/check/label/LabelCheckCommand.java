package fr.apiscol.metadata.scolomfr3utils.command.check.label;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.IScolomfrDomDocumentRequired;
import fr.apiscol.metadata.scolomfr3utils.command.ISkosApiRequired;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;

public class LabelCheckCommand extends AbstractCommand implements IScolomfrDomDocumentRequired, ISkosApiRequired {
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
		List<Pair<Node, Node>> labelValuePairs = getLabelvaluePairs();
		Iterator<Pair<Node, Node>> it = labelValuePairs.iterator();
		boolean valid = true;
		while (it.hasNext()) {
			Pair<Node, Node> pair = (Pair<Node, Node>) it.next();
			valid = valid & labelMatchesValue(pair);
		}

		return true;
	}

	private boolean labelMatchesValue(Pair<Node, Node> pair) {
		System.out.println("on vérifie");
		return false;
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
				Pair<Node, Node> pair = new ImmutablePair<Node, Node>(value, label);

				pairs.add(pair);
			}
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, e.getMessage());
		}
		return pairs;
	}

}
