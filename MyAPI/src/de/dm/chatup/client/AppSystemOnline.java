package de.dm.chatup.client;

import java.util.ArrayList;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
import de.dm.chatup.chat.Message;
import de.dm.chatup.client.KryonetClient;
import de.dm.chatup.network.Network;

public class AppSystemOnline  {
	
	static AppSystemOnline instance = null;
	private AppSystem appSystem;
	private KryonetClient kryonet;
	
	protected static AppSystemOnline getInstance(String serverLink, int port) {
		if(instance == null) {
			instance = new AppSystemOnline(serverLink, port);
		}
		return instance;
	}
	
	private AppSystemOnline(String serverLink, int port) {
		this.kryonet = KryonetClient.getInstance(serverLink, port);
		this.appSystem = AppSystem.getInstance();
	}
	
	protected void getInitialData(String lastUpdateFilePath, String messagesFilePath) {
		
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
				
				appSystem.readMessagesFromFiles(lastUpdateFilePath, messagesFilePath);

				// 3. Schritt: Nachrichten aus diesen Chats auslesen
				for (int anzChats = 0; anzChats < appSystem.getMyChats().size(); anzChats++) {

					int chatID = appSystem.getMyChats().get(anzChats).getChatID();
					Network.GetAllMessagesFromChat gcm = new Network.GetAllMessagesFromChat();
					gcm.chatID = chatID;
					gcm.lastUpdate = appSystem.getLastUpdate();
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
		
		this.appSystem.setLastUpdate(getDateFromServer());
	}
	
	protected void createNewChat(Network.AddNewChat anc) throws ClientCreateChatErrorException {
		
		if (kryonet.send(anc) && kryonet.getResult() instanceof Network.AddNewChat
				&& ((Network.AddNewChat)kryonet.getResult()).result != -1) {
			return;
			
		}
		throw new ClientCreateChatErrorException("Chat konnte nicht erstellt werden!");
	}
	
	protected int isUserExisting(String deviceID) throws ClientNotConnectedException {
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

		throw new ClientNotConnectedException("Keine Antwort vom Server erhalten. Besteht eine Internetverbindung?");
	}
	
	protected int createNewUser(String deviceID, String vorname, String nachname) throws ClientUserAddingErrorException {
		Network.AddUser au = new Network.AddUser();
		au.deviceID = deviceID;
		au.vorname = vorname;
		au.nachname = nachname;

		if (kryonet.send(au) && kryonet.getResult() instanceof Network.AddUser
				&& ((Network.AddUser)kryonet.getResult()).result != -1) {
			au.result = ((Network.AddUser)kryonet.getResult()).result;
			appSystem.addContact(new Contact(au.result, au.vorname, au.nachname));
			appSystem.setUserID(au.result);
			return au.result;
		}
		throw new ClientUserAddingErrorException("Benutzer konnte nicht hinzugefügt werden!");
	}
	
	protected void sendNewMessage(String nachricht) throws ClientMessageSendErrorException {
		Network.SendNewMessage snm = new Network.SendNewMessage();
		snm.chatID = appSystem.getOpenChat();
		snm.erstellerID = appSystem.getUserID();
		snm.nachricht = nachricht;
		if(kryonet.send(snm)) {
			return;
		}
		throw new ClientMessageSendErrorException("Nachricht konnte nicht gesendet werden!");
	}
	
	protected void addUserToChat(int chatID, int userID) throws ClientAddUserToChatErrorException {
		
		Network.AddUserToChat autc = new Network.AddUserToChat();
		autc.chatID = chatID;
		autc.userID = userID;
		if(kryonet.send(autc)) {
			return;
		}
		throw new ClientAddUserToChatErrorException("Benutzer konnte nicht zum Chat hinzugefügt werden!");
	}
	
	protected String getDateFromServer() {
		Network.GetDateFromServer gdfs = new Network.GetDateFromServer();

		if (kryonet.send(gdfs) && kryonet.getResult() instanceof Network.GetDateFromServer) {
			return ((Network.GetDateFromServer)kryonet.getResult()).result;
		} else {
			return "-1";
		}
	}
	
	protected AppSystem getAppSystem() {
		return this.appSystem;
	}
	
	protected KryonetClient getKryonetClient() {
		return this.kryonet;
	}

}
