package fr.apiscol.metadata.scolomfr3utils.command.check.syntax;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;
import fr.apiscol.metadata.scolomfr3utils.command.CommandException;
import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;

/**
 * 
 * Command that applies xsd validation to provided scolomfr file
 *
 */
public class XsdValidationCommand extends AbstractCommand {

	@Override
	public void execute() throws CommandException {
		Source source = new StreamSource(getScolomfrFile());
		try {
			getLogger().info("Xsd validation of file " + getScolomfrFile().getAbsolutePath());
			getXsdValidator().validate(source);
			getLogger().error("XSD validation success");
		} catch (SAXException e) {
			getLogger().error("XSD validation failure");
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		} catch (IOException e) {
			String message = "Unable to open scolomfr file " + getScolomfrFile().getAbsolutePath()
					+ " for xsd validation : " + e.getMessage();
			getLogger().error(e);
			throw new CommandFailureException(message);
		}
	}

	@Override
	public boolean isXsdRequired() {
		return true;
	}

	@Override
	public boolean isSkosRequired() {
		return false;
	}

	@Override
	public boolean isScolomfrFileRequired() {
		return true;
	}

}
