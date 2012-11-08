package com.dm.chatup.system;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.dm.chatup.activities.ChatActivity;
import com.dm.chatup.chat.*;
import com.dm.chatup.internet.KryonetClient;
import com.dm.chatup.internet.Network;

public class AppSystem {
	
	static AppSystem instance = null;
	List<Contact> myContacts;
	List<Chat> myChats;
	int myUserID;
	int myOpenChat;
	
	public static AppSystem getInstance() {
		if(instance == null) {
			instance = new AppSystem();
		}
		return instance;
	}
	
	private AppSystem() {
		this.myContacts = new ArrayList<Contact>();
		this.myChats = new ArrayList<Chat>();
	}
	
	public void addUserToChat(int userID, int chatID) {
		Contact searchedUser = null;
		for (int i = 0; i < this.myContacts.size(); i++) {
			if(this.myContacts.get(i).getUserID() == userID) {
				searchedUser = this.myContacts.get(i);
				break;
			}
		}
		if(searchedUser != null) {
			for (int i = 0; i < this.myChats.size(); i++) {
				if(this.myChats.get(i).getChatID() == chatID) {
					this.myChats.get(i).addUser(searchedUser);
					return;
				}
			}
		}
	}
	
	public Contact getUserFromID(int userID) {
		for (int i = 0; i < this.myContacts.size(); i++) {
			if(this.myContacts.get(i).getUserID() == userID) {
				return this.myContacts.get(i);
			}
		}
		return null;
	}
	
	public void addMessageToChat(Message msg) {
		for (int i = 0; i < this.myChats.size(); i++) {
			if(this.myChats.get(i).getChatID() == msg.getChatID()) {
				this.myChats.get(i).addMessage(msg);
				return;
			}
		}
	}
	
	public Chat getChatFromID(int chatID) {
		for (int i = 0; i < this.myChats.size(); i++) {
			if(this.myChats.get(i).getChatID() == chatID) {
				return myChats.get(i);
			}
		}
		return null;
	}
	
	public Notification makeNotification(String text, Activity quellKlasse, Class<?> zielKlasse) {
		Notification notification = new Notification(android.R.drawable.stat_notify_sync, text, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledOnMS=2000;
		notification.ledOffMS=1000;
		Intent i = new Intent(quellKlasse.getApplicationContext(), zielKlasse);
		PendingIntent contentIntent = PendingIntent.getActivity(quellKlasse.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(quellKlasse, "NotificationActivity", text, contentIntent);
		return notification;
	}
	
	public Notification makeNotification(String text, Activity quellKlasse, Class<?> zielKlasse, int chatID) {
		Notification notification = new Notification(android.R.drawable.stat_notify_sync, text, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledOnMS=2000;
		notification.ledOffMS=1000;
		Intent i = new Intent(quellKlasse.getApplicationContext(), zielKlasse);
		i.putExtra("chatID", chatID);
		PendingIntent contentIntent = PendingIntent.getActivity(quellKlasse.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(quellKlasse, "NotificationActivity", text, contentIntent);
		return notification;
	}
	
	public List<Chat> getMyChats() {
		return this.myChats;
	}
	
	public void setMyChats(List<Chat> chats) {
		this.myChats = chats;
	}
	
	public void addChat(Chat c) {
		this.myChats.add(c);
	}
	
	public List<Contact> getMyContacts() {
		return this.myContacts;
	}
	
	public void setMyContacts(List<Contact> contacts) {
		this.myContacts = contacts;
	}
	
	public void addContact(Contact c) {
		this.myContacts.add(c);
	}

	public void setOpenChat(int chatID) {
		this.myOpenChat = chatID;
	}
	
	public int getOpenChat() {
		return this.myOpenChat;
	}

	public int getUserID() {
		return this.myUserID;
	}
	
	public void setUserID(int userID) {
		this.myUserID = userID;
	}

	

}
