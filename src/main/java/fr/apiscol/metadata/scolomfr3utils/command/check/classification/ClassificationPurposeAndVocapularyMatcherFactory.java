package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

public class ClassificationPurposeAndVocapularyMatcherFactory {

	public IClassificationPurposeAndVocabularyMatcher getMatcher(String scolomfrVersion) throws Exception {
		switch (scolomfrVersion) {
		case "3.0":
			return new Scolomfr30ClassificationPurposeAndVocabularyMatcher();
		default:
			throw new Exception(
					"No classification purpose and vocabulary matcher available for version " + scolomfrVersion);
		}
	}

}
