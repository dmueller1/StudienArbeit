package com.dm.chatup.system;

import java.util.ArrayList;

import android.os.AsyncTask;

import com.dm.chatup.chat.Chat;
import com.dm.chatup.chat.Contact;
import com.dm.chatup.chat.Message;
import com.dm.chatup.internet.KryonetClient;
import com.dm.chatup.internet.Network;

public class AppSystemOnline  {
	
	static AppSystemOnline instance = null;
	AppSystem appSystem;
	KryonetClient kryonet;
	
	public static AppSystemOnline getInstance() {
		if(instance == null) {
			instance = new AppSystemOnline();
		}
		return instance;
	}
	
	private AppSystemOnline() {
		this.kryonet = KryonetClient.getInstance();
		this.appSystem = AppSystem.getInstance();
	}
	
	public void getInitialData() {
		
		//Bisherige Daten löschen
		appSystem.setMyContacts(new ArrayList<Contact>());
		appSystem.setMyChats(new ArrayList<Chat>());
		
		// 1. Schritt: Benutzer auslesen
		Network.GetFriends gf = new Network.GetFriends();
		if (kryonet.send(gf) && kryonet.getResult() instanceof Network.GetFriends
				&& ((Network.GetFriends)kryonet.getResult()).result != null) {
			String[][] benutzer = ((Network.GetFriends)kryonet.getResult()).result;
			int anz = benutzer.length;
			
			for (int i = 0; i < anz; i++) {
				Contact c = new Contact(Integer.valueOf(benutzer[i][0]),
						benutzer[i][1], benutzer[i][2]);
				appSystem.addContact(c);
			}

			// 2. Schritt: Chats auslesen
			Network.GetChatsFromUser guc = new Network.GetChatsFromUser();
			guc.userID = appSystem.getUserID();
			if (kryonet.send(guc) && kryonet.getResult() instanceof Network.GetChatsFromUser
					&& ((Network.GetChatsFromUser) kryonet.getResult()).result != null) {
				String[][] chats = ((Network.GetChatsFromUser) kryonet.getResult()).result;
				anz = chats.length;

				for (int i = 0; i < anz; i++) {
					Chat c = new Chat(Integer.valueOf(chats[i][0]), chats[i][1]);
					appSystem.addChat(c);
				}

				// 3. Schritt: Nachrichten aus diesen Chats auslesen
				for (int anzChats = 0; anzChats < appSystem.getMyChats().size(); anzChats++) {

					int chatID = appSystem.getMyChats().get(anzChats).getChatID();
					Network.GetAllMessagesFromChat gcm = new Network.GetAllMessagesFromChat();
					gcm.chatID = chatID;
					if (kryonet.send(gcm)
							&& kryonet.getResult() instanceof Network.GetAllMessagesFromChat
							&& ((Network.GetAllMessagesFromChat) kryonet.getResult()).result != null) {

						String[][] nachrichten = ((Network.GetAllMessagesFromChat) kryonet.getResult()).result;
						anz = nachrichten.length;
						
						for (int i = 0; i < anz; i++) {
							Message m = new Message(chatID, Integer.valueOf(nachrichten[i][1]), nachrichten[i][2], nachrichten[i][3]);
							appSystem.addMessageToChat(m);
						}
						
					}

					// 4. Schritt: Get UserIDs from Chats
					Network.GetUserIDsFromChat gcu = new Network.GetUserIDsFromChat();
					gcu.chatID = chatID;
					if (kryonet.send(gcu) && kryonet.getResult() instanceof Network.GetUserIDsFromChat
							&& ((Network.GetUserIDsFromChat) kryonet.getResult()).result != null) {
						String[] userIDs = ((Network.GetUserIDsFromChat) kryonet.getResult()).result;
						anz = userIDs.length;

						for (int i = 0; i < anz; i++) {
							appSystem.addUserToChat(Integer.valueOf(userIDs[i]), chatID);
						}
					}
				}
			}
		}
	}
	
	public boolean createNewChat(Network.AddNewChat anc) {
		if (kryonet.send(anc) && kryonet.getResult() instanceof Network.AddNewChat
				&& ((Network.AddNewChat)kryonet.getResult()).result != -1) {
			return true;
			
		}
		return false;
	}
	
	public int isUserExisting(String deviceID) {
		Network.IsUserExisting iur = new Network.IsUserExisting();
		iur.deviceID = deviceID;

		if (kryonet.send(iur) && kryonet.getResult() instanceof Network.IsUserExisting) {
			iur.result =  ((Network.IsUserExisting)kryonet.getResult()).result;
			
			if(iur.result > -1) {
				appSystem.setUserID(iur.result);
				return iur.result;
			} else {
				return -1;
			}
		} 

		return -2;
	}
	
	public boolean createNewUser(String deviceID, String vorname, String nachname) {
		Network.AddUser au = new Network.AddUser();
		au.deviceID = deviceID;
		au.vorname = vorname;
		au.nachname = nachname;

		if (kryonet.send(au) && kryonet.getResult() instanceof Network.AddUser
				&& ((Network.AddUser)kryonet.getResult()).result != -1) {
			au.result = ((Network.AddUser)kryonet.getResult()).result;
			appSystem.addContact(new Contact(au.result, au.vorname, au.nachname));
			appSystem.setUserID(au.result);
			return true;
		}
		return false;
	}
	
	public boolean sendNewMessage(int userID, int chatID, String nachricht) {
		Network.SendNewMessage snm = new Network.SendNewMessage();
		snm.chatID = chatID;
		snm.erstellerID = userID;
		snm.nachricht = nachricht;
		return kryonet.send(snm);
	}
	
	public boolean addUserToChat(int chatID, int userID) {
		
		Network.AddUserToChat autc = new Network.AddUserToChat();
		autc.chatID = chatID;
		autc.userID = userID;
		return kryonet.send(autc);
	}

}
