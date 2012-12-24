package de.dm.chatup.client;

import de.dm.chatup.network.Network.Contact;

public interface NewUserInChatEvent {
	
	public void reactOnNewUserInChat(Contact c, int chatID);

}
