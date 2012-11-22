package de.dm.chatup.client;

import de.dm.chatup.chat.Message;

public interface NewMessageEvent {
	
	public void reactOnNewMessage(Message m);

}
