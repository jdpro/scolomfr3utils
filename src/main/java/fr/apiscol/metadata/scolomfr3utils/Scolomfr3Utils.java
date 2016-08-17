package fr.apiscol.metadata.scolomfr3utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import fr.apiscol.metadata.scolomfr3utils.command.CommandFailureException;
import fr.apiscol.metadata.scolomfr3utils.command.ICommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.ClassificationCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.TaxonPathCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.XsdValidationCommand;
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
	private List<String> messages = new ArrayList<>();
	private String scolomfrVersion;
	private final ISkosApi skosApi = new SkosApi();
	private Validator validator;
	private boolean isValid;

	@Override
	public void setScolomfrFile(final File scolomfrFile) {
		this.scolomfrFile = scolomfrFile;
	}

	private void execute(ICommand command) {
		if (init(command)) {
			try {
				command.execute();
			} catch (CommandFailureException e) {
				getLogger().info(e);
				isValid = false;
				messages.addAll(e.getMessages());
			}
		} else {
			isValid = false;
			messages.add("Command " + command.getClass().getName() + " failed to initialize.");
		}

	}

	@Override
	public List<String> getMessages() {
		return messages;
	}

	@Override
	public Boolean isValid() {
		return isValid;
	}

	private boolean init(ICommand command) {
		isValid = true;
		messages = new ArrayList<>();
		if (StringUtils.isEmpty(scolomfrVersion)) {
			messages.add("Please specify the scolomfr major.minor version.");
			return false;
		}
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
			if (null == skosApi.getSkosModel()) {
				Model skosModel = new SkosLoader().loadSkos(scolomfrVersion);
				skosApi.setSkosModel(skosModel);
			}
			command.setSkosApi(skosApi);
		}
		return true;
	}

	private boolean checkScolomfrFile(ICommand command) {
		if (command.isScolomfrFileRequired()) {
			if (null == scolomfrFile) {
				messages.add("Please provide a scolomfr file before calling scolomfrutils methods.");
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
	public IScolomfr3Utils checkAll() {
		return checkXsd();
	}

	@Override
	public IScolomfr3Utils checkXsd() {
		execute(new XsdValidationCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkClassifications() {
		execute(new ClassificationCheckCommand());
		return this;
	}

	@Override
	public IScolomfr3Utils checkTaxonPaths() {
		execute(new TaxonPathCheckCommand());
		return this;
	}

}
