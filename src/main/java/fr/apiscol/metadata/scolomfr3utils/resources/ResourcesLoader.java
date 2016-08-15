package fr.apiscol.metadata.scolomfr3utils.resources;

import java.io.InputStream;

/*
 * Utility class to load any file from classpath
 */
public class ResourcesLoader {
	/**
	 * Get inputstream from file path
	 * 
	 * @param path
	 *            Absolute or relative path
	 * @return The ressource as inputSteam
	 */
	public InputStream loadResource(final String path) {
		return ResourcesLoader.class.getResourceAsStream(path);

	}

	/**
	 * System id, for resources that contains relative links (like includes in
	 * xsd files)
	 * 
	 * @param path
	 *            Absolute or relative path of the resource loaded with
	 *            loadResource
	 * @return The system id
	 */
	public String getSystemId(final String path) {
		return ResourcesLoader.class.getResource(path).toString();
	}
}
