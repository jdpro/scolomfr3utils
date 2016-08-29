package fr.apiscol.metadata.scolomfr3utils.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown each time a command fails. Carries message to output
 */
public abstract class CommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final List<String> messages = new ArrayList<>();

	/**
	 * Thrown when a command fails to perform its task. Carries message to be
	 * displayed to final user.
	 * 
	 * @param message
	 *            The messages
	 */
	public CommandException(String message) {
		super(message);
		this.messages.add(message);

	}

	/**
	 * Thrown when a command fails to perform its task. Carries a list of
	 * messages to be displayed to final user.
	 * 
	 * @param messages
	 *            The messages
	 */
	public CommandException(List<String> messages) {
		super(messages.toString());
		this.messages.addAll(messages);
	}

	/**
	 * The reason(s) of failure
	 * 
	 * @return the messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * Set reasons of validation failure
	 * 
	 * @param messages
	 *            The reason(s) of failure
	 */
	public void setMessages(List<String> messages) {
		this.messages.addAll(messages);
	}

}
