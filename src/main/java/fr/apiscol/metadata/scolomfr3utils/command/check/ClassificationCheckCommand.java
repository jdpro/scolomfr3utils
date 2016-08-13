package fr.apiscol.metadata.scolomfr3utils.command.check;

import fr.apiscol.metadata.scolomfr3utils.command.AbstractCommand;

public class ClassificationCheckCommand extends AbstractCommand {
	@Override
	public void execute() throws CommandFailureException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isXsdRequired() {
		return false;
	}

	@Override
	public boolean isSkosRequired() {
		return true;
	}

	@Override
	public boolean isScolomfrFileRequired() {
		return true;
	}

}
