package com.dm.chatup.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Environment;

import com.dm.chatup.activities.ChatActivity;
import com.dm.chatup.chat.*;
import com.dm.chatup.internet.KryonetClient;
import com.dm.chatup.internet.Network;

public class AppSystem {
	
	final String lastUpdateFilePath = Environment.getExternalStorageDirectory() + "/lastUpdate.cuf";
	final String messagesFilePath = Environment.getExternalStorageDirectory() + "/messages.cuf";
	static AppSystem instance = null;
	List<Contact> myContacts;
	List<Chat> myChats;
	int myUserID;
	int myOpenChat;
	String lastUpdate = "-1";
	Message lastMessage = null;
	
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
	
	public String getLastUpdate() {
		return this.lastUpdate;
	}
	
	public void setLastUpdate(String lu) {
		this.lastUpdate = lu;
	}
	
	public void setLastMessage(Message m) {
		this.lastMessage = m;
	}
	
	public Message getLastMessage() {
		return this.lastMessage;
	}

	public boolean readMessagesFromFiles() {
		
		File lastUpdateFile = new File(lastUpdateFilePath);
		File messages = new File(this.messagesFilePath);
		
		if(lastUpdateFile.exists() && messages.exists()) {
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(lastUpdateFile));
				
				if(br.ready()) {
					this.lastUpdate = br.readLine();
					br.close();
				} else {
					br.close();
					return false;
				}
				
				br = new BufferedReader(new FileReader(messages));
				
				while(br.ready()) {
					String[] messageAsString = br.readLine().split(";");
					
					if(messageAsString.length == 4) {
						if(messageAsString.length > 4) {
							for(int i = 4; i < messageAsString.length; i++) {
								messageAsString[3] += messageAsString[i]+";";
							}
						}
						Message m = new Message(Integer.valueOf(messageAsString[0]), Integer.valueOf(messageAsString[1]), messageAsString[2], messageAsString[3]);
						addMessageToChat(m);
					} 
				}
				br.close();
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
		
	}
	
	public boolean writeMessagesToFile() {
		
		Message lastMessage = getLastMessage();
		String lastUpdate = getLastUpdate();
		
		if(lastMessage != null || lastUpdate != "-1") {
		
			try {
				File lastUpdateFile = new File(lastUpdateFilePath);
				File messages = new File(this.messagesFilePath);
				
				if(!lastUpdateFile.exists()) {
					if(!lastUpdateFile.createNewFile()) {
						return false;
					}
				}
				
				if(!messages.exists()) {
					if(!messages.createNewFile()) {
						return false;
					}
				}
				
						FileWriter fw = new FileWriter(lastUpdateFile);
						
						if(lastMessage != null) {
							fw.write(lastMessage.getErstellDatum());
						} else {
							if(lastUpdate != "-1") {
								fw.write(lastUpdate);
							} else {
								fw.close();
								return false;
							}
						}
						
						fw.flush();
						fw.close();
						
						fw = new FileWriter(messages);
						
						for(int chats = 0; chats < getMyChats().size(); chats++) {
							
							for(int nachrichten = 0; nachrichten < getMyChats().get(chats).getMessages().size(); nachrichten++) {
								Message m = getMyChats().get(chats).getMessages().get(nachrichten);
								fw.write(m.getChatID()+";"+m.getErstellerID()+";"+m.getErstellDatum()+";"+m.getNachricht()+"\n");
							}
							
						}	
						
						fw.flush();
						fw.close();
				
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}

	

}
