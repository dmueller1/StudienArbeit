package com.dm.chatup.events;

import com.dm.chatup.chat.Message;

public interface NewMessageEvent {
	
	public void reactOnNewMessage(Message m);

}
