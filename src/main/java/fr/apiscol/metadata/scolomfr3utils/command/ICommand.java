package fr.apiscol.metadata.scolomfr3utils.command;

import java.util.List;

public interface ICommand {

	boolean execute();

	void setScolomfrVersion(final String scolomfrVersion);

	String getScolomfrVersion();

	List<String> getMessages(MessageStatus status);

	List<String> getMessages();

}
