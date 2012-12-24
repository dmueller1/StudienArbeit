package de.dm.chatup.client;

import de.dm.chatup.network.Network.Chat;

public interface NewChatEvent {
	
	public void reactOnNewChat(Chat c);

}
