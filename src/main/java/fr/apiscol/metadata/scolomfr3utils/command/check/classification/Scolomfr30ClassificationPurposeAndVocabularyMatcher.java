package fr.apiscol.metadata.scolomfr3utils.command.check.classification;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.atlas.lib.MultiMap;

public class Scolomfr30ClassificationPurposeAndVocabularyMatcher implements IClassificationPurposeAndVocabularyMatcher {

	private static final Map<String, String> purposeVocabularyCorrespondency;
	static {
		purposeVocabularyCorrespondency = new HashMap<String, String>();
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-003",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-015");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-021",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-016");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-018",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-018");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-011",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-021");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/educational_level",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-022");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/discipline",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-014");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-004",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-030");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-013",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-045");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-014",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-046");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-016",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-029");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-017",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-041");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-019",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-042");
		purposeVocabularyCorrespondency.put("http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-028-num-023",
				"http://data.education.fr/voc/scolomfr/scolomfr-voc-043");

	}

	@Override
	public String getVocabularyId(String purpose) {
		return purposeVocabularyCorrespondency.get(purpose);
	}

}
