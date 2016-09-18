package fr.apiscol.metadata.scolomfr3utils.xsd;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.Configuration;
import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.resources.ResourcesLoader;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

/**
 * Utility class to create xsd validator from xsd file located in classpath
 */
public class ValidatorLoader {

	static final String NO_XSD_FILE_PATH_PROVIDED_IN_CONFIGURATION_FOR_VERSION = "No xsd file path provided in configuration for version %s";
	private Logger logger;

	/**
	 * Load the xsd file corresponding to this scolomfr version. Matching
	 * between version and files is in config file.
	 * 
	 * @param scolomfrVersion
	 *            The xsd scolomfr version
	 * @return Xsd Validator
	 */
	public Validator loadXsd(SchemaVersion scolomfrVersion) {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		String xsdFilePath = Configuration.getInstance().getXsdFilePath(scolomfrVersion.toString());
		if (StringUtils.isEmpty(xsdFilePath)) {
			getLogger().error(String.format(NO_XSD_FILE_PATH_PROVIDED_IN_CONFIGURATION_FOR_VERSION, scolomfrVersion));
			return null;
		}
		InputStream in = new ResourcesLoader().loadResource(xsdFilePath);
		if (in == null) {
			getLogger().error("Unable to load xsd file " + xsdFilePath + " for version " + scolomfrVersion);
			return null;
		}
		String systemId = new ResourcesLoader().getSystemId(xsdFilePath);
		Source schemaSource = new StreamSource(in, systemId);
		Schema schema = null;
		Validator validator;
		try {
			schema = factory.newSchema(schemaSource);

			validator = schema.newValidator();
		} catch (SAXException e) {
			getLogger().error("The scolomfr xsd files seems to be corrupted or not available on " + xsdFilePath);
			getLogger().error(e);
			return null;
		}
		return validator;
	}

	Logger getLogger() {
		if (logger == null) {
			logger = LoggerProvider.getLogger(this.getClass());
		}
		return logger;
	}

}
