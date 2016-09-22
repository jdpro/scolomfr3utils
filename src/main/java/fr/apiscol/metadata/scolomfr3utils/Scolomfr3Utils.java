package fr.apiscol.metadata.scolomfr3utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.command.ICommand;
import fr.apiscol.metadata.scolomfr3utils.command.IScolomfrDomDocumentRequired;
import fr.apiscol.metadata.scolomfr3utils.command.IScolomfrFileRequired;
import fr.apiscol.metadata.scolomfr3utils.command.ISkosApiRequired;
import fr.apiscol.metadata.scolomfr3utils.command.IXsdValidatorRequired;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.ClassificationPurposesCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.TaxonPathCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.TaxonPathVocabCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.label.LabelCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.syntax.XsdValidationCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.vcard.VcardCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosLoader;
import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersionHandler;
import fr.apiscol.metadata.scolomfr3utils.version.VersionDetectionException;
import fr.apiscol.metadata.scolomfr3utils.xsd.ValidatorLoader;

/**
 * 
 * {@inheritDoc}
 *
 */
public class Scolomfr3Utils implements IScolomfr3Utils {

	static final String UNABLE_TO_DETERMINE_SCOLOMFR_VERSION = "Scolomfr version is neither specified as command line argument neither provided as metadataSchema tag in scolomfr file.";
	private Logger logger;
	private File scolomfrFile;
	private Map<MessageStatus, List<String>> messages = new EnumMap<>(MessageStatus.class);
	private SchemaVersion scolomfrVersion;
	private final ISkosApi skosApi = new SkosApi();
	private Validator validator;
	private boolean isValid;
	private Document scolomfrDocument;

	private boolean versionDetectionPerformed = false;

	/**
	 * Main class constructor
	 */
	public Scolomfr3Utils() {
		reset();
	}

	@Override
	public void setScolomfrFile(final File scolomfrFile) {
		reset();
		this.scolomfrFile = scolomfrFile;
		this.scolomfrDocument = null;
	}

	@Override
	public void reset() {
		isValid = true;
		versionDetectionPerformed = false;
		initMessages(MessageStatus.FAILURE);
		initMessages(MessageStatus.WARNING);
	}

	private void execute(ICommand command) {
		if (init(command)) {
			boolean commandIsValid = command.execute();
			isValid = isValid && commandIsValid;
			messages.get(MessageStatus.FAILURE).addAll(command.getMessages(MessageStatus.FAILURE));
			messages.get(MessageStatus.WARNING).addAll(command.getMessages(MessageStatus.WARNING));
		} else {
			isValid = false;
			messages.get(MessageStatus.FAILURE)
					.add("Command " + command.getClass().getName() + " failed to initialize.");
		}

	}

	private void initMessages(MessageStatus status) {
		ArrayList<String> messagesList = new ArrayList<>();
		messages.put(status, messagesList);
	}

	@Override
	public List<String> getMessages(MessageStatus status) {
		return messages.get(status);
	}

	@Override
	public Boolean isValid() {
		return isValid;
	}

	private boolean init(ICommand command) {
		if (scolomfrVersion == null && !versionDetectionPerformed) {
			detectScolomfrVersion();
		}
		if (scolomfrVersion == null) {
			messages.get(MessageStatus.FAILURE).add(
					UNABLE_TO_DETERMINE_SCOLOMFR_VERSION);
			return false;
		}
		command.setScolomfrVersion(scolomfrVersion);
		return checkScolomfrFile(command) && checkScolomfrDomDocument(command) && loadSkos(command) && loadXsd(command);
	}

	private void detectScolomfrVersion() {
		if (loadScolomfrFileAsDocument()) {
			SchemaVersionHandler schemaVersionHandler = SchemaVersionHandler.getInstance();
			try {
				scolomfrVersion = schemaVersionHandler.getMostRecentVersionFromDocument(scolomfrDocument);
			} catch (VersionDetectionException e) {
				getLogger().warn(e);
				messages.get(MessageStatus.WARNING)
						.add("Unable to detect scolomfr version from file : " + e.getMessage());
			} finally {
				versionDetectionPerformed = true;
			}
		}
	}

	private boolean loadXsd(ICommand command) {
		if (command instanceof IXsdValidatorRequired) {
			if (null == validator) {
				validator = new ValidatorLoader().loadXsd(scolomfrVersion);
			}
			if (null == validator) {
				getLogger().error("Unable to load xsd file for version " + scolomfrVersion);
				return false;
			}
			((IXsdValidatorRequired) command).setXsdValidator(validator);
		}

		return true;
	}

	private boolean loadSkos(ICommand command) {
		if (command instanceof ISkosApiRequired) {
			loadSkos();
			((ISkosApiRequired) command).setSkosApi(skosApi);
		}
		return true;
	}

	private void loadSkos() {
		if (null == skosApi.getSkosModel()) {
			Model skosModel = new SkosLoader().loadSkos(scolomfrVersion);
			skosApi.setSkosModel(skosModel);
		}
	}

	private boolean checkScolomfrFile(ICommand command) {
		if (command instanceof IScolomfrFileRequired) {
			if (null == scolomfrFile) {
				messages.get(MessageStatus.FAILURE)
						.add("Please provide a scolomfr file before calling scolomfrutils methods.");
				return false;
			}
			((IScolomfrFileRequired) command).setScolomfrFile(scolomfrFile);
		}
		return true;
	}

	private boolean checkScolomfrDomDocument(ICommand command) {
		if (command instanceof IScolomfrDomDocumentRequired) {
			if (loadScolomfrFileAsDocument()) {
				((IScolomfrDomDocumentRequired) command).setScolomfrDocument(scolomfrDocument);
				return true;
			} else {
				return false;
			}
		}
		return true;

	}

	private boolean loadScolomfrFileAsDocument() {
		if (null == scolomfrFile) {
			messages.get(MessageStatus.FAILURE)
					.add("Please provide a scolomfr file before calling scolomfrutils methods.");
			return false;
		}
		if (!buildScolomfrDocument()) {
			messages.get(MessageStatus.FAILURE).add("Unable to build Dom document from provided xml file..");
			return false;
		}
		return true;
	}

	protected boolean buildScolomfrDocument() {
		if (scolomfrDocument != null) {
			return true;
		}
		try {
			scolomfrDocument = DomDocumentWithLineNumbersBuilder.getInstance().parse(scolomfrFile);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			getLogger().error(e);
			return false;
		}
		return true;
	}

	/**
	 * Indicates the version of Scolomfr with which scolomfrutils must work
	 * 
	 * @param versionStr
	 *            A string "major.minor" (e.g. 3.0)
	 */
	@Override
	public void setScolomfrVersion(String versionStr) {
		SchemaVersion newVersion = SchemaVersion.fromString(versionStr);
		// If version has changed, unload the model and register new version
		if (scolomfrVersion == null || (newVersion != null && !newVersion.equals(scolomfrVersion))) {
			skosApi.setSkosModel(null);
			scolomfrVersion = newVersion;
		}

	}

	private Logger getLogger() {
		if (logger == null) {
			logger = LoggerProvider.getLogger(this.getClass());
		}
		return logger;
	}

	@Override
	public ISkosApi getSkosApi() {
		loadSkos();
		return skosApi;
	}

	@Override
	public IScolomfr3Utils checkAll() {
		return checkXsd().checkLabels().checkClassifications().checkVcards();
	}

	@Override
	public IScolomfr3Utils checkXsd() {
		execute(new XsdValidationCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkClassifications() {
		return checkTaxonPaths().checkClassificationPurposes().checkTaxonPathVocabs();
	}

	@Override
	public IScolomfr3Utils checkTaxonPaths() {
		execute(new TaxonPathCheckCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkClassificationPurposes() {
		execute(new ClassificationPurposesCheckCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkTaxonPathVocabs() {
		execute(new TaxonPathVocabCheckCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkLabels() {
		execute(new LabelCheckCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkVcards() {
		execute(new VcardCheckCommand());
		return this;
	}

}
