package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;

import javax.xml.validation.Validator;

import org.apache.jena.rdf.model.Model;

import fr.apiscol.metadata.scolomfr3utils.command.check.CommandFailureException;

public interface ICommand {

	void execute() throws CommandFailureException;

	boolean isXsdRequired();

	void setXsdValidator(final Validator validator);

	boolean isSkosRequired();

	void setSkosModel(final Model skosModel);

	boolean isScolomfrFileRequired();

	void setScolomfrFile(final File scolomfrFile);

}
