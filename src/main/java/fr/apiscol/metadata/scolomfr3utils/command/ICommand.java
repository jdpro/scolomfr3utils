package fr.apiscol.metadata.scolomfr3utils.command;

import java.io.File;

import javax.xml.validation.Validator;

import fr.apiscol.metadata.scolomfr3utils.skos.ISkosApi;

public interface ICommand {

	void execute() throws CommandException;

	boolean isXsdRequired();

	void setXsdValidator(final Validator validator);

	boolean isSkosRequired();

	void setSkosApi(final ISkosApi skosApi);

	boolean isScolomfrFileRequired();

	void setScolomfrFile(final File scolomfrFile);

	void setScolomfrVersion(final String scolomfrVersion);

	String getScolomfrVersion();

}
