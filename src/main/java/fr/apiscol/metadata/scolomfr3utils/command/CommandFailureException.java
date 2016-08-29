package fr.apiscol.metadata.scolomfr3utils.command;

import java.util.List;

/**
 * Thrown each time a command fails. Carries message to output
 */
public class CommandFailureException extends CommandException {

	public CommandFailureException(List<String> messages) {
		super(messages);
	}

	public CommandFailureException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
