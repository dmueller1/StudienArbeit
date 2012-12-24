package de.dm.chatup.client;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Message;

public interface NewMessageEvent {
	
	public void reactOnNewMessage(Chat chat, Message m);

}
