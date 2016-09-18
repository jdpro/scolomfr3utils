package fr.apiscol.metadata.scolomfr3utils.command.check.vcard;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.ValidationWarnings;
import ezvcard.Warning;
import ezvcard.io.chain.ChainingTextStringParser;
import ezvcard.property.VCardProperty;
import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.IScolomfrDomDocumentRequired;
import fr.apiscol.metadata.scolomfr3utils.command.ISkosApiRequired;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

public class VcardCheckCommand extends AbstractCommand implements IScolomfrDomDocumentRequired, ISkosApiRequired {

	private static final String IMPOSSIBLE_TO_EXTRACT_VCARD_VERSION = "Impossible to extract vcard version line %s";
	private static final String STRING_CANNOT_BE_PARSED_AS_VCARD = "Provided string can't be parsed as vcard line %s";

	@Override
	public boolean execute() {
		NodeList vcardEntities = vcardEntities();
		boolean vcardsAreValid = true;
		for (int i = 0; i < vcardEntities.getLength(); i++) {
			Node vcardEntity = vcardEntities.item(i);
			boolean vcardIsValid = vcardIsValid(vcardEntity);
			vcardsAreValid = vcardsAreValid && vcardIsValid;
		}
		return vcardsAreValid;
	}

	private boolean vcardIsValid(Node vcardEntity) {
		String vcardString = vcardEntity.getTextContent();
		ChainingTextStringParser vCardS = Ezvcard.parse(vcardString);
		VCard vcard = vCardS.first();
		if (vcard == null) {
			addMessage(MessageStatus.FAILURE, String.format(STRING_CANNOT_BE_PARSED_AS_VCARD,
					vcardEntity.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
			return false;
		}

		VCardVersion vcardVersion = vcard.getVersion();
		if (vcardVersion == null) {
			addMessage(MessageStatus.FAILURE, String.format(IMPOSSIBLE_TO_EXTRACT_VCARD_VERSION,
					vcardEntity.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)));
			return false;
		}
		ValidationWarnings validatioWarnings = vcard.validate(vcardVersion);
		Iterator<Entry<VCardProperty, List<Warning>>> it = validatioWarnings.iterator();
		while (it.hasNext()) {
			Map.Entry<VCardProperty, java.util.List<Warning>> entry = it.next();
			List<Warning> warnings = entry.getValue();
			Iterator<Warning> it2 = warnings.iterator();
			while (it2.hasNext()) {
				Warning warning = it2.next();
				addMessage(MessageStatus.WARNING,
						"Entity line " + vcardEntity.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY)
								+ " : " + warning.getMessage());
			}
		}
		return true;
	}

	private NodeList vcardEntities() {
		NodeList vcardEntities = null;
		try {
			vcardEntities = (NodeList) xPath.evaluate("//contribute/entity", scolomfrDocument, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, e.getMessage());
		}
		return vcardEntities;
	}

}
