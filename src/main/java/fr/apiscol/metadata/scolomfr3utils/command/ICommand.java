package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;

import javax.xml.validation.Validator;

import org.apache.jena.rdf.model.Model;

public interface ICommand {

	void execute() throws CommandFailureException;

	boolean isXsdRequired();

	void setXsdValidator(final Validator validator);

	boolean isSkosRequired();

	void setSkosModel(final Model skosModel);

	boolean isScolomfrFileRequired();

	void setScolomfrFile(final File scolomfrFile);

}
