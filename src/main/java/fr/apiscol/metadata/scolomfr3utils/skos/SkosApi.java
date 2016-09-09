package fr.apiscol.metadata.scolomfr3utils.skos;

import org.apache.commons.lang3.StringUtils;

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
	private static final String NO_RESULT = "NO_RESULT";
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
			Statement statement = (Statement) stmts.next();
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
		Statement next = stmts.next();
		RDFNode typeObject = next.getObject();
		if (!typeObject.isResource()) {
			return false;
		}
		String typeStr = typeObject.asResource().getURI();
		return StringUtils.equalsIgnoreCase(typeStr, HTTP_WWW_W3_ORG_2004_02_SKOS_CORE + "Collection");
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
		Property prefLabel = getSkosModel().getProperty("http://www.w3.org/2004/02/skos/core#", "prefLabel");
		Property altLabel = getSkosModel().getProperty("http://www.w3.org/2004/02/skos/core#", "altLabel");
		Selector prefLabelSelector = new SimpleSelector(subject, prefLabel, (RDFNode) null);
		Selector altLabelSelector = new SimpleSelector(subject, altLabel, (RDFNode) null);
		StmtIterator stmts1 = getSkosModel().listStatements(prefLabelSelector);
		StmtIterator stmts2 = getSkosModel().listStatements(altLabelSelector);
		ExtendedIterator<Statement> stmts = stmts1.andThen(stmts2);
		while (stmts.hasNext()) {
			Statement statement = (Statement) stmts.next();
			String label = ((Literal) statement.getObject()).getString();
			if (StringUtils.equals(label, resourceLabel)) {
				return true;
			}
		}
		return false;
	}

}
