package fr.apiscol.metadata.scolomfr3utils.skos;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.apiscol.metadata.scolomfr3utils.Configuration;
import fr.apiscol.metadata.scolomfr3utils.log.LoggerProvider;
import fr.apiscol.metadata.scolomfr3utils.resources.ResourcesLoader;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

/**
 * Utility class to create Jena skos model from Skos file located in classpath
 */
public class SkosLoader {

	private Logger logger;

	/**
	 * Load the skos model corresponding to this scolomfr version. Matching
	 * between version and files is in config file.
	 * 
	 * @param scolomfrVersion
	 *            Major.minor version as string
	 * @return The Jena skos model
	 */
	public Model loadSkos(SchemaVersion scolomfrVersion) {
		String skosFilePath = Configuration.getInstance().getSkosFilePath(scolomfrVersion.toString());
		if (StringUtils.isEmpty(skosFilePath)) {
			getLogger().error("No registered Skos file for version " + scolomfrVersion);
			return null;
		}
		getLogger().info("Loading skos file from " + skosFilePath);

		InputStream in = new ResourcesLoader().loadResource(skosFilePath);
		if (in == null) {
			getLogger().error("Unable to load skos file " + skosFilePath + " for version " + scolomfrVersion);
			return null;
		}
		Model skosModel = ModelFactory.createDefaultModel();
		skosModel.read(in, null);
		return skosModel;
	}

	private Logger getLogger() {
		if (logger == null) {
			logger = LoggerProvider.getLogger(this.getClass());
		}
		return logger;
	}
}
