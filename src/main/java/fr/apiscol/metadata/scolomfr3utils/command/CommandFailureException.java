package fr.apiscol.metadata.scolomfr3utils.command;

import java.util.ArrayList;
import java.util.List;

public class CommandFailureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> messages;

	public CommandFailureException(String message) {
		super(message);
		messages = new ArrayList<>();
		this.messages.add(message);

	}

	public CommandFailureException(List<String> messages) {
		super(messages.toString());
		this.messages.addAll(messages);
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
