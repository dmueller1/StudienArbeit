package com.dm.chatup.events;

import android.app.NotificationManager;

import com.dm.chatup.chat.Chat;

public interface NewChatEvent {
	
	public void reactOnNewChat(Chat c);

}
