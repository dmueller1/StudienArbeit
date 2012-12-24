package de.dm.chatup.client;

import de.dm.chatup.network.Network.Contact;

public interface NewUserEvent {
	
	public void reactOnNewUser(Contact c);

}
