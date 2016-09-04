package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import fr.apiscol.metadata.scolomfr3utils.VersionHandlingException;

public class ClassificationPurposeAndVocapularyMatcherFactory {

	public IClassificationPurposeAndVocabularyMatcher getMatcher(String scolomfrVersion) throws VersionHandlingException {
		switch (scolomfrVersion) {
		case "3.0":
			return new Scolomfr30ClassificationPurposeAndVocabularyMatcher();
		default:
			throw new VersionHandlingException(
					"No classification purpose and vocabulary matcher available for version " + scolomfrVersion);
		}
	}

}
