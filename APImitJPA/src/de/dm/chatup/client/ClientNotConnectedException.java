package de.dm.chatup.client;

@SuppressWarnings("serial")
public class ClientNotConnectedException extends Exception {

	public ClientNotConnectedException(String message) {
		super(message);
	}

}
