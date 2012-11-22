package de.dm.chatup.client;

import de.dm.chatup.chat.Contact;

public interface NewUserEvent {
	
	public void reactOnNewUser(Contact c);

}
