package de.dm.chatup.server;

@SuppressWarnings("serial")
public class ServerActionErrorException extends Exception {

	public ServerActionErrorException(String message) {
		super(message);
	}

}
