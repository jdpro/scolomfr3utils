package fr.apiscol.metadata.scolomfr3utils.skos;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class SkosApi implements ISkosApi {
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

}
