package fr.apiscol.metadata.scolomfr3utils.command.check;

import java.util.ArrayList;
import java.util.List;

public class CommandFailureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> messages = new ArrayList<>();

	public CommandFailureException(String message) {
		super(message);
		messages.add(message);
		
	}

	public CommandFailureException(List<String> messages) {
		super(messages.toString());
		messages.addAll(messages);
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
