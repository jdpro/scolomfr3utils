package fr.apiscol.metadata.scolomfr3utils.skos;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class SkosApi implements ISkosApi {
	private static final String HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	static final String NO_RESULT = "NO_RESULT";
	private static final String HTTP_WWW_W3_ORG_2004_02_SKOS_CORE = "http://www.w3.org/2004/02/skos/core#";
	private Model skosModel;

	@Override
	public Model getSkosModel() {
		return skosModel;
	}

	@Override
	public void setSkosModel(Model skosModel) {
		this.skosModel = skosModel;
	}

	@Override
	public boolean isBroaderThan(final String subjectUri, final String objectUri) {
		Resource subject = getSkosModel().getResource(subjectUri);
		Resource object = getSkosModel().getResource(objectUri);
		Property narrowerProperty = getSkosModel().getProperty(HTTP_WWW_W3_ORG_2004_02_SKOS_CORE, "narrower");
		Selector narrowerPropertySelector = new SimpleSelector(subject, narrowerProperty, object);
		StmtIterator stmts = getSkosModel().listStatements(narrowerPropertySelector);
		return stmts.hasNext();
	}

	@Override
	public String getPrefLabelForResource(final String subjectUri) {
		Resource subject = getSkosModel().getResource(subjectUri);
		Property prefLabel = getSkosModel().getProperty(HTTP_WWW_W3_ORG_2004_02_SKOS_CORE, "prefLabel");
		Selector selector = new SimpleSelector(subject, prefLabel, (RDFNode) null);
		StmtIterator stmts = getSkosModel().listStatements(selector);
		while (stmts.hasNext()) {
			Statement statement = stmts.next();
			return ((Literal) statement.getObject()).getString();
		}
		return NO_RESULT;
	}

	@Override
	public boolean resourceExists(final String subjectUri) {
		Resource subject = getSkosModel().getResource(subjectUri);
		Selector selector = new SimpleSelector(subject, null, (RDFNode) null);
		StmtIterator stmts = getSkosModel().listStatements(selector);
		return stmts.hasNext();
	}

	@Override
	public boolean vocabularyExists(String subjectUri) {
		Resource subject = getSkosModel().getResource(subjectUri);
		Property type = getSkosModel().getProperty(HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS, "type");
		Selector selector = new SimpleSelector(subject, type, (RDFNode) null);
		StmtIterator stmts = getSkosModel().listStatements(selector);
		if (!stmts.hasNext()) {
			return false;
		}
		while (stmts.hasNext()) {
			Statement next = stmts.next();
			RDFNode typeObject = next.getObject();
			if (!typeObject.isResource()) {
				continue;
			}
			String typeStr = typeObject.asResource().getURI();
			if (StringUtils.equalsIgnoreCase(typeStr, HTTP_WWW_W3_ORG_2004_02_SKOS_CORE + "Collection")) {
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean resourceIsMemberOfVocabulary(String resourceUri, String vocabUri) {
		Resource subject = getSkosModel().getResource(vocabUri);
		Resource object = getSkosModel().getResource(resourceUri);
		Property memberProperty = getSkosModel().getProperty(HTTP_WWW_W3_ORG_2004_02_SKOS_CORE, "member");
		Selector memberPropertySelector = new SimpleSelector(subject, memberProperty, object);
		StmtIterator stmts = getSkosModel().listStatements(memberPropertySelector);
		return stmts.hasNext();
	}

	@Override
	public boolean resourceHasLabel(String resourceUri, String resourceLabel) {
		Resource subject = getSkosModel().getResource(resourceUri);
		Property prefLabel = getSkosModel().getProperty(HTTP_WWW_W3_ORG_2004_02_SKOS_CORE, "prefLabel");
		Property altLabel = getSkosModel().getProperty(HTTP_WWW_W3_ORG_2004_02_SKOS_CORE, "altLabel");
		Selector prefLabelSelector = new SimpleSelector(subject, prefLabel, (RDFNode) null);
		Selector altLabelSelector = new SimpleSelector(subject, altLabel, (RDFNode) null);
		StmtIterator stmts1 = getSkosModel().listStatements(prefLabelSelector);
		StmtIterator stmts2 = getSkosModel().listStatements(altLabelSelector);
		ExtendedIterator<Statement> stmts = stmts1.andThen(stmts2);
		while (stmts.hasNext()) {
			Statement statement = stmts.next();
			String label = ((Literal) statement.getObject()).getString();
			if (StringUtils.equals(label, resourceLabel)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getVocabUriByLabel(final String vocabLabel) {
		final String collection = HTTP_WWW_W3_ORG_2004_02_SKOS_CORE + "Collection";
		Property type = getSkosModel().getProperty(HTTP_WWW_W3_ORG_1999_02_22_RDF_SYNTAX_NS, "type");
		Selector selector = new SimpleSelector(null, type, (RDFNode) null) {
			@Override
			public boolean selects(Statement s) {
				if (!s.getObject().isResource()) {
					return false;
				}
				String objectUri = s.getObject().asResource().getURI();
				if (!StringUtils.equalsIgnoreCase(objectUri, collection)) {
					return false;
				}
				return resourceHasLabel(s.getSubject().getURI(), vocabLabel);
			}
		};

		StmtIterator stmts = getSkosModel().listStatements(selector);
		if (!stmts.hasNext()) {
			return null;
		}
		Statement next = stmts.next();
		return next.getSubject().getURI();
	}

	@Override
	public String detectCommonVocabularyForTaxonNodes(List<Node> taxonNodesList) {
		Node taxonNode;
		String commonVocabUri = null;
		for (int i = 0; i < taxonNodesList.size(); i++) {
			taxonNode = taxonNodesList.get(i);
			String taxonUri = taxonNode.getTextContent().trim();
			String taxonVocabUri = getResourceVocabulary(taxonUri);
			if (StringUtils.isEmpty(commonVocabUri)) {
				commonVocabUri = taxonVocabUri;
			} else {
				if (!StringUtils.equalsIgnoreCase(commonVocabUri, taxonVocabUri)) {
					commonVocabUri = null;
					break;
				}
			}
		}
		return commonVocabUri;
	}

	@Override
	public String getResourceVocabulary(String resourceUri) {
		Resource object = getSkosModel().getResource(resourceUri);
		Property memberProperty = getSkosModel().getProperty(HTTP_WWW_W3_ORG_2004_02_SKOS_CORE, "member");
		Selector memberPropertySelector = new SimpleSelector(null, memberProperty, object);
		StmtIterator stmts = getSkosModel().listStatements(memberPropertySelector);
		if (stmts.hasNext()) {
			Statement next = stmts.nextStatement();
			return next.getSubject().getURI();
		}
		return null;
	}

}
