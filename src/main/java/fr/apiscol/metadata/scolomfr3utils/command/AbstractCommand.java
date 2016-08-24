package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;

/**
 * 
 * @{inheritDoc}
 *
 */
public abstract class AbstractCommand implements ICommand {

	private static DocumentBuilder domDocumentBuilder;
	private Validator xsdValidator;
	private ISkosApi skosApi;
	private File scolomfrFile;
	protected Document scolomfrDocument;
	private Logger logger;

	@Override
	public void setXsdValidator(Validator xsdValidator) {
		this.xsdValidator = xsdValidator;
	}

	protected Validator getXsdValidator() {
		return xsdValidator;
	}

	@Override
	public void setSkosApi(final ISkosApi skosApi) {
		this.skosApi = skosApi;
	}

	protected ISkosApi getSkosApi() {
		return skosApi;
	}

	@Override
	public void setScolomfrFile(File scolomfrFile) {
		this.scolomfrFile = scolomfrFile;
	}

	protected File getScolomfrFile() {
		return scolomfrFile;
	}

	protected Logger getLogger() {
		if (logger == null) {
			logger = LoggerProvider.getLogger(this.getClass());
		}
		return logger;
	}

	protected void buildScolomfrDocument() throws CommandFailureException {
		try {
			if (domDocumentBuilder == null) {
				domDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			}
			scolomfrDocument = domDocumentBuilder.parse(getScolomfrFile());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			getLogger().error(e);
			throw new CommandFailureException(e.getMessage());
		}
	}
}
