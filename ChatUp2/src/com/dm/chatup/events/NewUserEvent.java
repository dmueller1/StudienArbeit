package com.dm.chatup.events;

import com.dm.chatup.chat.Contact;

public interface NewUserEvent {
	
	public void reactOnNewUser(Contact c);

}
