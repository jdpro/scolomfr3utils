package fr.apiscol.metadata.scolomfr3utils.resources;

import java.io.InputStream;

public class ResourcesLoader {
	public InputStream loadResource(final String path) {
		return ResourcesLoader.class.getResourceAsStream(path);

	}

	public String getSystemId(final String path) {
		return ResourcesLoader.class.getResource(path).toString();
	}
}
