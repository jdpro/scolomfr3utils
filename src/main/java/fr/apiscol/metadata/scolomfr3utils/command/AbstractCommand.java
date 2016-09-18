package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

public abstract class AbstractCommand implements ICommand {

	private Validator xsdValidator;
	private ISkosApi skosApi;
	private File scolomfrFile;
	protected Document scolomfrDocument;

	private Logger logger;
	private final XPathFactory xpathFactory = XPathFactory.newInstance();
	protected final XPath xPath = xpathFactory.newXPath();
	private SchemaVersion scolomfrVersion;
	private Map<MessageStatus, List<String>> messages = new EnumMap<>(MessageStatus.class);

	protected AbstractCommand() {
		initMessages(MessageStatus.FAILURE);
		initMessages(MessageStatus.WARNING);
	}

	private void initMessages(MessageStatus status) {
		ArrayList<String> messagesList = new ArrayList<>();
		messages.put(status, messagesList);
	}

	@Override
	public SchemaVersion getScolomfrVersion() {
		return scolomfrVersion;
	}

	@Override
	public void setScolomfrVersion(final SchemaVersion scolomfrVersion) {
		this.scolomfrVersion = scolomfrVersion;
	}

	public void setXsdValidator(Validator xsdValidator) {
		this.xsdValidator = xsdValidator;
	}

	protected Validator getXsdValidator() {
		return xsdValidator;
	}

	public void setSkosApi(final ISkosApi skosApi) {
		this.skosApi = skosApi;
	}

	protected ISkosApi getSkosApi() {
		return skosApi;
	}

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

	@Override
	public List<String> getMessages(MessageStatus status) {
		return messages.get(status);
	}

	@Override
	public List<String> getMessages() {
		List<String> allMessages = new ArrayList<>();
		for (MessageStatus status : MessageStatus.values()) {
			allMessages.addAll(getMessages(status));
		}
		return allMessages;
	}

	protected void addMessage(MessageStatus status, String message) {
		messages.get(status).add(message);
	}

	public void setScolomfrDocument(Document scolomfrDocument) {
		this.scolomfrDocument = scolomfrDocument;
	}

}
