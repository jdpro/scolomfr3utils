package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;

import javax.xml.validation.Validator;

import org.apache.log4j.Logger;

import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;

/**
 * 
 * @{inheritDoc}
 *
 */
public abstract class AbstractCommand implements ICommand {

	private Validator xsdValidator;
	private ISkosApi skosApi;
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
}
