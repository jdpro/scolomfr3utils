package fr.apiscol.metadata.scolomfr3utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.command.ICommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.ClassificationCheckCommand;
import fr.apiscol.metadata.scolomfr3utils.command.check.CommandFailureException;
import fr.apiscol.metadata.scolomfr3utils.command.check.XsdValidationCommand;
import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.resources.ResourcesLoader;

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
	private Model skosModel;
	private Validator validator;
	private boolean isValid;

	@Override
	public void setScolomfrFile(final File scolomfrFile) {
		this.scolomfrFile = scolomfrFile;
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

	private void execute(ICommand command) {
		if (init(command)) {
			try {
				command.execute();
			} catch (CommandFailureException e) {
				getLogger().info(e);
				isValid = false;
				messages.addAll(e.getMessages());
			}
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
				SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				String xsdFilePath = Configuration.getInstance().getXsdFilePath(scolomfrVersion);
				getLogger().info("Loading xsd file from " + xsdFilePath);
				InputStream in = new ResourcesLoader().loadResource(xsdFilePath);
				if (in == null) {
					getLogger().error("Unable to load xsd file " + xsdFilePath + " for version " + scolomfrVersion);
					return false;
				}
				String systemId = new ResourcesLoader().getSystemId(xsdFilePath);
				Source schemaSource = new StreamSource(in, systemId);
				Schema schema = null;
				try {
					schema = factory.newSchema(schemaSource);

					validator = schema.newValidator();
				} catch (SAXException e) {
					getLogger()
							.error("The scolomfr xsd files seems to be corrupted or not available on " + xsdFilePath);
					getLogger().error(e);
					return false;
				}
			}
			command.setXsdValidator(validator);
		}

		return true;
	}

	private boolean loadSkos(ICommand command) {
		if (command.isSkosRequired()) {
			if (null == skosModel) {

				String skosFilePath = Configuration.getInstance().getSkosFilePath(scolomfrVersion);
				getLogger().info("Loading skos file from " + skosFilePath);
				InputStream in = new ResourcesLoader().loadResource(skosFilePath);
				if (in == null) {
					getLogger().error("Unable to load skos file " + skosFilePath + " for version " + scolomfrVersion);
					return false;
				}
				skosModel = ModelFactory.createDefaultModel();
				skosModel.read(in, null);
			}
			command.setSkosModel(skosModel);
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

	@Override
	public void setScolomfrVersion(String version) {
		// If version has changed, unload the model
		if (version != scolomfrVersion) {
			skosModel = null;
		}
		scolomfrVersion = version;
	}

	private Logger getLogger() {
		if (logger == null) {
			logger = LoggerProvider.getLogger(this.getClass());
		}
		return logger;
	}

}
