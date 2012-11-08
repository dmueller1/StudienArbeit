package com.dm.chatup.events;

import com.dm.chatup.chat.Contact;

public interface NewUserInChatEvent {
	
	public void reactOnNewUserInChat(Contact c, int userID);

}
