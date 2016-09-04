package fr.apiscol.metadata.scolomfr3utils;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.command.ICommand;
import fr.apiscol.metadata.scolomfr3utils.command.MessageStatus;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.ClassificationPurposesCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.TaxonPathCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.classification.TaxonPathVocabCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.syntax.XsdValidationCommand;
import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosApi;
import fr.apiscol.metadata.scolomfr3utils.skos.SkosLoader;
import fr.apiscol.metadata.scolomfr3utils.xsd.ValidatorLoader;

/**
 * 
 * {@inheritDoc}
 *
 */
public class Scolomfr3Utils implements IScolomfr3Utils {

	private Logger logger;
	private File scolomfrFile;
	private Map<MessageStatus, List<String>> messages = new EnumMap<>(MessageStatus.class);
	private String scolomfrVersion;
	private final ISkosApi skosApi = new SkosApi();
	private Validator validator;
	private boolean isValid;

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
	}

	@Override
	public void reset() {
		isValid = true;
		initMessages(MessageStatus.FAILURE);
		initMessages(MessageStatus.WARNING);
	}

	private void execute(ICommand command) {
		if (init(command)) {
			isValid = isValid && command.execute();
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
		if (StringUtils.isEmpty(scolomfrVersion)) {
			messages.get(MessageStatus.FAILURE).add("Please specify the scolomfr major.minor version.");
			return false;
		}
		command.setScolomfrVersion(scolomfrVersion);
		return checkScolomfrFile(command) && loadSkos(command) && loadXsd(command);
	}

	private boolean loadXsd(ICommand command) {
		if (command.isXsdRequired()) {
			if (null == validator) {
				validator = new ValidatorLoader().loadXsd(scolomfrVersion);
			}
			if (null == validator) {
				getLogger().error("Unable to load xsd file for version " + scolomfrVersion);
				return false;

			}
			command.setXsdValidator(validator);
		}

		return true;
	}

	private boolean loadSkos(ICommand command) {
		if (command.isSkosRequired()) {
			loadSkos();
			command.setSkosApi(skosApi);
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
		if (command.isScolomfrFileRequired()) {
			if (null == scolomfrFile) {
				messages.get(MessageStatus.FAILURE)
						.add("Please provide a scolomfr file before calling scolomfrutils methods.");
				return false;
			}
			command.setScolomfrFile(scolomfrFile);
		}

		return true;
	}

	/**
	 * Indicates the version of Scolomfr with which scolomfrutils must work
	 * 
	 * @param version
	 *            A string "major.minor" (e.g. 3.0)
	 */
	@Override
	public void setScolomfrVersion(String version) {
		// If version has changed, unload the model
		if (version != scolomfrVersion) {
			skosApi.setSkosModel(null);
		}
		scolomfrVersion = version;
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
		return checkXsd().checkClassifications().checkTaxonPathVocab();
	}

	@Override
	public IScolomfr3Utils checkXsd() {
		execute(new XsdValidationCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkClassifications() {
		return checkTaxonPaths().checkClassificationPurposes();
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
	public IScolomfr3Utils checkTaxonPathVocab() {
		execute(new TaxonPathVocabCheckCommand());
		return this;
	}

}
