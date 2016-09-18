package fr.apiscol.metadata.scolomfr3utils.version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Handles schema version concerns
 */
public class SchemaVersionHandler {

	private static SchemaVersionHandler instance;
	private final XPathFactory xpathFactory = XPathFactory.newInstance();
	protected final XPath xPath = xpathFactory.newXPath();

	private static final Pattern versionPattern = Pattern.compile("([0-9]{1,2})\\.([0-9]{1,2})");

	private SchemaVersionHandler() {
	}

	/**
	 * Singleton method
	 * 
	 * @return unique instance
	 */
	public static SchemaVersionHandler getInstance() {
		if (instance == null) {
			instance = new SchemaVersionHandler();
		}
		return instance;
	}

	/**
	 * Guess version from any string containing major.minor schema
	 * 
	 * @param versionStr
	 *            version as string, e.g. "SCOLOMFRv2.0"
	 * @return null if provided string does not match pattern major.minor
	 */
	public SchemaVersion getVersionFromString(String versionStr) {
		SchemaVersion version = null;
		Matcher m = versionPattern.matcher(versionStr);
		if (m.matches()) {
			int major = Integer.parseInt(m.group(1));
			int minor = Integer.parseInt(m.group(2));
			version = new SchemaVersion(major, minor);
		}
		return version;
	}

	/**
	 * 
	 * @param scolomfrDocument
	 *            Dom document to search in
	 * @return most recent version
	 * @throws VersionDetectionException
	 *             If version detection fails
	 */
	public SchemaVersion getMostRecentVersionFromDocument(Document scolomfrDocument) throws VersionDetectionException {
		NodeList metadataSchemaTags = getMetadataSchemaTags(scolomfrDocument);
		if (metadataSchemaTags.getLength() == 0) {
			throw new VersionDetectionException("Missing metaMetadata.metadataSchema in scolomfr File.");
		}
		List<SchemaVersion> versions = new ArrayList<>();
		for (int i = 0; i < metadataSchemaTags.getLength(); i++) {
			Node metadataSchemaTag = metadataSchemaTags.item(i);
			String metadataSchema = metadataSchemaTag.getTextContent().trim();
			SchemaVersion version = getVersionFromString(metadataSchema);
			if (version != null) {
				versions.add(version);
			}

		}
		if (versions.isEmpty()) {
			throw new VersionDetectionException(
					"None of the metaMetadata.metadataSchema tags provided in the file contains the pattern major.minor");
		}
		// SchemaVersion implements comparable
		Collections.sort(versions);
		// return last (biggest) element
		return versions.get(versions.size() - 1);
	}

	private NodeList getMetadataSchemaTags(Document scolomfrDocument) throws VersionDetectionException {
		NodeList metadataSchemaTags = null;
		try {
			metadataSchemaTags = (NodeList) xPath.evaluate("/metaMetadata/metadataSchema", scolomfrDocument,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new VersionDetectionException(e);
		}
		return metadataSchemaTags;
	}

}
