package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import fr.apiscol.metadata.scolomfr3utils.VersionHandlingException;
import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

public class ClassificationPurposeAndVocapularyMatcherFactory {

	public IClassificationPurposeAndVocabularyMatcher getMatcher(SchemaVersion schemaVersion)
			throws VersionHandlingException {
		if (schemaVersion == null) {
			return null;
		}
		return getMatcher(schemaVersion.toString());
	}

	public IClassificationPurposeAndVocabularyMatcher getMatcher(String schemaVersion) throws VersionHandlingException {
		switch (schemaVersion) {
		case "3.0":
			return new Scolomfr30ClassificationPurposeAndVocabularyMatcher();
		default:
			throw new VersionHandlingException(
					"No classification purpose and vocabulary matcher available for version " + schemaVersion);
		}
	}

}
