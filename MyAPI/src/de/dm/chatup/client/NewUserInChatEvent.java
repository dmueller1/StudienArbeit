package de.dm.chatup.client;

import de.dm.chatup.chat.Contact;

public interface NewUserInChatEvent {
	
	public void reactOnNewUserInChat(Contact c, int chatID);

}
