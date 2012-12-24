package de.dm.chatup.client;

import java.io.IOException;
import java.util.List;


import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;
import de.dm.chatup.network.Network.Message;
import de.dm.chatup.network.Network;

public class ChatUpClient {
	
	AppSystemOnline appSystemOnline;
	static ChatUpClient instance = null;
	
	public static void main(String[] args) {
		
	}
	
	public ChatUpClient() {
		String a = "";
		a+="22";
	}
	protected ChatUpClient(String serverLink, int port) {
		appSystemOnline = AppSystemOnline.getInstance(serverLink, port);
	}
	
	public static ChatUpClient getInstance(String serverLink, int port) {
		if(instance==null) {
			instance = new ChatUpClient(serverLink, port);
		}
		return instance;
	}
	
	public int anmelden(String deviceID) throws ClientNotConnectedException {
		
		int userID = appSystemOnline.isUserExisting(deviceID);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}

	public int addDeviceToUser(String oldDeviceID, String newDeviceID) throws ClientNotConnectedException {
		
		int userID = appSystemOnline.addDeviceToUser(oldDeviceID, newDeviceID);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}

	public int registerNewUser(String deviceID, String firstname,
			String lastname) throws ClientUserAddingErrorException {
		int userID = appSystemOnline.createNewUser(deviceID, firstname, lastname);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}
	
	public void getInitialChatDataFromServer(String lastUpdateFilePath, String messagesFilePath) {
		appSystemOnline.getInitialData(lastUpdateFilePath, messagesFilePath);
	}
	
//	public void writeMessagesAndLastUpdateFile(String lastUpdateFilePath, String messagesFilePath) {
//		//appSystemOnline.getAppSystem().writeMessagesToFile(lastUpdateFilePath, messagesFilePath);
//	}

	public void sendNewMessage(String message) throws ClientMessageSendErrorException {
		appSystemOnline.sendNewMessage(message);
	}

	public void addNewChat(Network.AddNewChat anc) throws ClientCreateChatErrorException {
		appSystemOnline.createNewChat(anc);
	}

	public void addUserToChat(int chatID, int userID) throws ClientAddUserToChatErrorException {
		appSystemOnline.addUserToChat(chatID, userID);
	}
	
	public List<Contact> getAllContactsInSystem() {
		return appSystemOnline.getAppSystem().getMyContacts();
	}
	
	public List<Chat> getAllMyChats() {
		return appSystemOnline.getAppSystem().getMyChats();
	}
	
	public Contact getUserFromID(int userID) {
		return appSystemOnline.getAppSystem().getUserFromID(userID);
	}
	
	public void addMessageToChat(Message m, Chat c) {
		appSystemOnline.getAppSystem().addMessageToChat(m, c);
	}
	
	public Chat getChatFromID(int chatID) {
		return appSystemOnline.getAppSystem().getChatFromID(chatID);
	}
	
	public int getMyUserID() {
		return appSystemOnline.getAppSystem().getUserID();
	}
	
	public int getActualChatID() {
		return appSystemOnline.getAppSystem().getOpenChat();
	}
	
	public void setActualChatID(int chatID) {
		appSystemOnline.getAppSystem().setOpenChat(chatID);
	}
	
//	public boolean reconnectClient(String serverLink, int port) throws IOException, InterruptedException {
//		return appSystemOnline.getKryonetClient().reconnectClient(serverLink, port);
//	}
	
	public boolean isClientConnected() {
		return appSystemOnline.getKryonetClient().getClient().isConnected();
	}
}
