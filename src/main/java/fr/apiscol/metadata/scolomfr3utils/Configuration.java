package fr.apiscol.metadata.scolomfr3utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.resources.ResourcesLoader;

/**
 * 
 * Wrapper for XML configuration file
 *
 */
public class Configuration implements IConfiguration {
	private static final String XML_CONFIG_FILE = "/config.xml";
	private Document xmlConfig;
	private static Configuration instance = null;

	private Configuration() throws ConfigurationException {
		loadXMLConfigurationFile();
	}

	public static IConfiguration getInstance() {
		if (instance == null) {
			try {
				instance = new Configuration();
			} catch (ConfigurationException e) {
				getLogger().error("Configuration loading error : ");
				getLogger().error(e);
			}
		}
		return instance;
	}

	private void loadXMLConfigurationFile() throws ConfigurationException {
		InputStream configFile = null;

		try {
			configFile = new ResourcesLoader().loadResource(XML_CONFIG_FILE);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			xmlConfig = builder.parse(configFile);

		} catch (SAXException | IOException | ParserConfigurationException | IllegalArgumentException e) {
			getLogger().error(e);
			throw new ConfigurationException(e);
		} finally {
			if (configFile != null) {
				try {
					configFile.close();
				} catch (IOException e) {
					getLogger().error(e);
				}
			}

		}

		if (xmlConfig == null) {
			getLogger().error("Failure : unable to load configuration file.");
		}
	}

	private static Logger getLogger() {
		return LoggerProvider.getLogger(Configuration.class);
	}

	@Override
	public Map<String, Pair<String, String>> commandLineOptions() {
		Element commandLineOptions = (Element) xmlConfig.getDocumentElement().getElementsByTagName("command-line")
				.item(0);
		NodeList features = commandLineOptions.getElementsByTagName("feature");
		Map<String, Pair<String, String>> commandLineOptionsDictionary = new HashMap<>();
		for (int i = 0; i < features.getLength(); i++) {
			Element feature = (Element) features.item(i);
			Element option = (Element) feature.getElementsByTagName("option").item(0);
			Element method = (Element) feature.getElementsByTagName("method").item(0);
			Element message = (Element) feature.getElementsByTagName("message").item(0);
			Pair<String, String> pair = new ImmutablePair<>(method.getTextContent(), message.getTextContent());
			commandLineOptionsDictionary.put(option.getTextContent(), pair);
		}
		return commandLineOptionsDictionary;

	}

	@Override
	public String getSkosFilePath(final String scolomfrVersion) {
		return getFilePath(scolomfrVersion, "skos");
	}

	@Override
	public String getXsdFilePath(final String scolomfrVersion) {
		return getFilePath(scolomfrVersion, "xsd");
	}

	private String getFilePath(final String scolomfrVersion, final String fileType) {
		Element configDocumentElement = xmlConfig.getDocumentElement();
		NodeList schemas = configDocumentElement.getElementsByTagName("schema");
		for (int i = 0; i < schemas.getLength(); i++) {
			Element schema = (Element) schemas.item(i);
			Element version = (Element) schema.getElementsByTagName("version").item(0);
			Element path = (Element) schema.getElementsByTagName(fileType).item(0);
			if (StringUtils.equals(version.getTextContent(), scolomfrVersion)) {
				return path.getTextContent();
			}
		}
		return null;
	}

	@Override
	public boolean versionIsSupported(String scolomfrVersion) {
		return StringUtils.isNotEmpty(getSkosFilePath(scolomfrVersion))
				&& StringUtils.isNotEmpty(getXsdFilePath((scolomfrVersion)));
	}

}
