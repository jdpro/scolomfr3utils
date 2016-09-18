package fr.apiscol.metadata.scolomfr3utils.command;

import java.util.List;

import fr.apiscol.metadata.scolomfr3utils.version.SchemaVersion;

public interface ICommand {

	boolean execute();

	void setScolomfrVersion(final SchemaVersion scolomfrVersion);

	SchemaVersion getScolomfrVersion();

	List<String> getMessages(MessageStatus status);

	List<String> getMessages();

}
