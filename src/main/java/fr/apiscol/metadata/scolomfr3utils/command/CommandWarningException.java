package fr.apiscol.metadata.scolomfr3utils.command;

import java.util.List;

public class CommandWarningException extends CommandException {

	public CommandWarningException(List<String> messages) {
		super(messages);
	}

	public CommandWarningException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
