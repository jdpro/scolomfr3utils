package fr.apiscol.metadata.scolomfr3utils.command;

public enum MessageStatus {
	FAILURE("failure"), WARNING("failure");
	private String value;

	private MessageStatus(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
