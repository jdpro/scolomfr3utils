package fr.apiscol.metadata.scolomfr3utils.command.check.syntax;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.IScolomfrFileRequired;
import fr.apiscol.metadata.scolomfr3utils.command.IXsdValidatorRequired;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;

/**
 * 
 * Command that applies xsd validation to provided scolomfr file
 *
 */
public class XsdValidationCommand extends AbstractCommand implements IScolomfrFileRequired, IXsdValidatorRequired {

	@Override
	public boolean execute() {
		Source source = new StreamSource(getScolomfrFile());
		try {
			getLogger().info("Xsd validation of file " + getScolomfrFile().getAbsolutePath());
			getXsdValidator().validate(source);
			getLogger().info("XSD validation success");
		} catch (SAXException e) {
			getLogger().error("XSD validation failure");
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, e.getMessage());
			return false;
		} catch (IOException e) {
			String message = "Unable to open scolomfr file " + getScolomfrFile().getAbsolutePath()
					+ " for xsd validation : " + e.getMessage();
			getLogger().error(e);
			addMessage(MessageStatus.FAILURE, message);
			return false;
		}
		return true;
	}

}
