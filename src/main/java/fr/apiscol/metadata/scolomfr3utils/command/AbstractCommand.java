package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;

/**
 * 
 * @{inheritDoc}
 *
 */
abstract public class AbstractCommand implements ICommand {

	private static DomDocumentWithLineNumbersBuilder domDocumentBuilder;
	private Validator xsdValidator;
	private ISkosApi skosApi;
	private File scolomfrFile;
	protected Document scolomfrDocument;
	private Logger logger;
	private final XPathFactory xpathFactory = XPathFactory.newInstance();
	protected final XPath xPath = xpathFactory.newXPath();
	private String scolomfrVersion;
	private Map<MessageStatus, List<String>> messages = new HashMap<>();

	public AbstractCommand() {
		initMessages(MessageStatus.FAILURE);
		initMessages(MessageStatus.WARNING);
	}

	private void initMessages(MessageStatus status) {
		ArrayList<String> messagesList = new ArrayList<>();
		messages.put(status, messagesList);
	}

	@Override
	public String getScolomfrVersion() {
		return scolomfrVersion;
	}

	@Override
	public void setScolomfrVersion(final String scolomfrVersion) {
		this.scolomfrVersion = scolomfrVersion;
	}

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

	protected boolean buildScolomfrDocument() {
		if (domDocumentBuilder == null) {
			domDocumentBuilder = new DomDocumentWithLineNumbersBuilder();
		}
		try {
			scolomfrDocument = domDocumentBuilder.parse(getScolomfrFile());
		} catch (IOException | SAXException | ParserConfigurationException e) {
			getLogger().error(e);
			return false;
		}
		return true;
	}

	@Override
	public List<String> getMessages(MessageStatus status) {
		return messages.get(status);
	}

	protected void addMessage(MessageStatus status, String message) {
		messages.get(status).add(message);
	}

}
