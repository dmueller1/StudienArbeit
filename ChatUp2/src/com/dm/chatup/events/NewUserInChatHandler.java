package com.dm.chatup.events;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Vibrator;

import com.dm.chatup.activities.ChatActivity;
import com.dm.chatup.activities.ContactActivity;
import com.dm.chatup.activities.NewChatActivity;
import com.dm.chatup.activities.R;
import com.dm.chatup.chat.Chat;
import com.dm.chatup.chat.Contact;
import com.dm.chatup.system.AppSystem;

public class NewUserInChatHandler {
	static NewUserInChatHandler instance = null;
	List<NewUserInChatEvent> allListener = new ArrayList<NewUserInChatEvent>();

	public static NewUserInChatHandler getInstance() {
		if(instance == null) {
			instance = new NewUserInChatHandler();
		}
		return instance;
	}
	
	public void addListener(NewUserInChatEvent evt) {
		this.allListener.add(evt);
	}
	
	public void notifyAllListener(final Contact c, final int chatID) {
		Chat isOneOfMyChats = AppSystem.getInstance().getChatFromID(chatID);
		if(isOneOfMyChats != null) {
			//Füge UserID per ChatID zum Chat hinzu
			AppSystem.getInstance().addUserToChat(c.getUserID(), chatID);
			for (int i = 0; i < this.allListener.size(); i++) {
				notify(allListener.get(i), c, chatID);
			}
		}
	}

	private void notify(NewUserInChatEvent toNotify, final Contact c, final int chatID) {
		if(toNotify instanceof ContactActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
			final ContactActivity ca = (ContactActivity)toNotify;
			ca.findViewById(R.id.listView1).post(new Runnable() {
				public void run() {
					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(500);
					ca.reactOnNewUserInChat(c, chatID);
				}
			});
		} else if (toNotify instanceof ChatActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
			final ChatActivity ca = (ChatActivity)toNotify;
			ca.findViewById(R.id.listView1).post(new Runnable() {
				public void run() {
					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(500);
					ca.reactOnNewUserInChat(c, chatID);
				}
			});
		} else if (toNotify instanceof NewChatActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
			final NewChatActivity ca = (NewChatActivity)toNotify;
			ca.findViewById(R.id.list_contacts).post(new Runnable() {
				public void run() {
					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
					v.vibrate(500);
					ca.reactOnNewUserInChat(c, chatID);
				}
			});
		} 
	}

}
