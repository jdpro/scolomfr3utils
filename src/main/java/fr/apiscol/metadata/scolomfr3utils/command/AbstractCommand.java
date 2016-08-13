package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;

import javax.xml.validation.Validator;

import org.apache.jena.rdf.model.Model;
import org.apache.log4j.Logger;

import fr.apiscol.metadata.scolomfr3utils.command.ICommand;
import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;

abstract public class AbstractCommand implements ICommand {

	private Validator xsdValidator;
	private Model skosModel;
	private File scolomfrFile;
	private Logger logger;

	@Override
	public void setXsdValidator(Validator xsdValidator) {
		this.xsdValidator = xsdValidator;
	}

	protected Validator getXsdValidator() {
		return xsdValidator;
	}

	@Override
	public void setSkosModel(Model skosModel) {
		this.skosModel = skosModel;
	}

	protected Model getSkosModel() {
		return skosModel;
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
}
