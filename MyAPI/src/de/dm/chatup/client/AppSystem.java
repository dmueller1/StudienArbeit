package de.dm.chatup.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
import de.dm.chatup.chat.Message;

public class AppSystem {
	
	static AppSystem instance = null;
	List<Contact> myContacts;
	List<Chat> myChats;
	int myUserID;
	int myOpenChat;
	String lastUpdate = "-1";
	Message lastMessage = null;
	
	protected static AppSystem getInstance() {
		if(instance == null) {
			instance = new AppSystem();
		}
		return instance;
	}
	
	private AppSystem() {
		this.myContacts = new ArrayList<Contact>();
		this.myChats = new ArrayList<Chat>();
	}
	
	protected void addUserToChat(int userID, int chatID) {
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
	
	protected Contact getUserFromID(int userID) {
		for (int i = 0; i < this.myContacts.size(); i++) {
			if(this.myContacts.get(i).getUserID() == userID) {
				return this.myContacts.get(i);
			}
		}
		return null;
	}
	
	protected void addMessageToChat(Message msg) {
		for (int i = 0; i < this.myChats.size(); i++) {
			if(this.myChats.get(i).getChatID() == msg.getChatID()) {
				this.myChats.get(i).addMessage(msg);
				return;
			}
		}
	}
	
	protected Chat getChatFromID(int chatID) {
		for (int i = 0; i < this.myChats.size(); i++) {
			if(this.myChats.get(i).getChatID() == chatID) {
				return myChats.get(i);
			}
		}
		return null;
	}
	
	protected List<Chat> getMyChats() {
		return this.myChats;
	}
	
	protected void setMyChats(List<Chat> chats) {
		this.myChats = chats;
	}
	
	protected void addChat(Chat c) {
		this.myChats.add(c);
	}
	
	protected List<Contact> getMyContacts() {
		return this.myContacts;
	}
	
	protected void setMyContacts(List<Contact> contacts) {
		this.myContacts = contacts;
	}
	
	protected void addContact(Contact c) {
		this.myContacts.add(c);
	}

	protected void setOpenChat(int chatID) {
		this.myOpenChat = chatID;
	}
	
	protected int getOpenChat() {
		return this.myOpenChat;
	}

	protected int getUserID() {
		return this.myUserID;
	}
	
	protected void setUserID(int userID) {
		this.myUserID = userID;
	}
	
	protected String getLastUpdate() {
		return this.lastUpdate;
	}
	
	protected void setLastUpdate(String lu) {
		this.lastUpdate = lu;
	}
	
	protected void setLastMessage(Message m) {
		this.lastMessage = m;
	}
	
	protected Message getLastMessage() {
		return this.lastMessage;
	}

	protected boolean readMessagesFromFiles(String lastUpdateFilePath, String messagesFilePath) {
		
		File lastUpdateFile = new File(lastUpdateFilePath);
		File messages = new File(messagesFilePath);
		
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
	
	protected boolean writeMessagesToFile(String lastUpdateFilePath, String messagesFilePath) {
		
		Message lastMessage = getLastMessage();
		String lastUpdate = getLastUpdate();
		
		if(lastMessage != null || lastUpdate != "-1") {
		
			try {
				File lastUpdateFile = new File(lastUpdateFilePath);
				File messages = new File(messagesFilePath);
				
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
