package de.dm.chatup.client;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.dm.chatup.chat.*;
import de.dm.chatup.network.Network;

public class KryonetClient {
	
	static KryonetClient instance = null;
	//AppSystem mySystem;
	Object resultOfCommunication;
	boolean empfangen;
	Client client;
	
	protected static KryonetClient getInstance(String serverLink, int port) {
		if(instance == null) {
			instance = new KryonetClient(serverLink, port);
		}
		return instance;
	}
	
	protected KryonetClient(String serverLink, int port) {
		
		try {
			this.empfangen = false;
			client = new Client();
			client.start();
			Network.register(client);
			

			client.addListener(new Listener() {
			
				public void received(Connection connection, Object object) {
					
					setEmpfangen(false);
					resultOfCommunication = object;
					
					if (object instanceof Network.AddUser
							&& ((Network.AddUser) object).result > -1) {
						Contact c = new Contact(
								((Network.AddUser) object).result,
								((Network.AddUser) object).vorname,
								((Network.AddUser) object).nachname);
						setEmpfangen(true);
						NewUserHandler.getInstance().notifyAllListener(c);

					} else if (object instanceof Network.SendNewMessage) {
						Message m = new Message(
								((Network.SendNewMessage) object).chatID,
								((Network.SendNewMessage) object).erstellerID,
								((Network.SendNewMessage) object).erstellDatum,
								((Network.SendNewMessage) object).nachricht);
						setEmpfangen(true);
						NewMessageHandler.getInstance().notifyAllListener(m);
						
					} else if (object instanceof Network.AddNewChat) {
						Chat c = new Chat(((Network.AddNewChat)object).result, ((Network.AddNewChat)object).name);
						if(((Network.AddNewChat)object).userIDs != null) {
							for(int i = 0; i < ((Network.AddNewChat)object).userIDs.length; i++) {
								Contact contact = AppSystem.getInstance().getUserFromID(Integer.valueOf(((Network.AddNewChat)object).userIDs[i]));
								c.addUser(contact);
							}
						}
						setEmpfangen(true);
						NewChatHandler.getInstance().notifyAllListener(c);
						
						
					} else if (object instanceof Network.AddUserToChat) {
						Contact c = AppSystem.getInstance().getUserFromID(Integer.valueOf(((Network.AddUserToChat)object).userID));
						NewUserInChatHandler.getInstance().notifyAllListener(c, Integer.valueOf(((Network.AddUserToChat)object).chatID));
					
					} else if (object instanceof Network.IsUserExisting) {
					} else if (object instanceof Network.GetFriends) {
					} else if (object instanceof Network.GetChatsFromUser) {
					} else if (object instanceof Network.GetUserIDsFromChat) {
					} else if (object instanceof Network.GetAllMessagesFromChat) {
					} else if (object instanceof Network.GetDateFromServer) {
					} else {
						setEmpfangen(false);
						return;
					}
					
					setEmpfangen(true);

				}

			});

			client.connect(5000, serverLink, port);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected boolean send(Object obj)  {
		try {
			int zaehler = 0;
			client.sendTCP(obj);

			while (!isEmpfangen()) {

				if (zaehler > 200) {
					System.out.println("Server antwortet nicht; keine Internetverbindung aktiv?");
					setEmpfangen(false);
					return false;
				}
				Thread.sleep(50);
				zaehler++;
			}
			setEmpfangen(false);
			return true;

		} catch (InterruptedException e) {
			e.printStackTrace();
			setEmpfangen(false);
			return false;
		}
	}
	
	protected boolean reconnectClient(String serverLink, int port) throws IOException, InterruptedException {
		if(!client.isConnected()) {
			client.connect(5000, serverLink, port);
			
				int zaehler = 0;
				while (!client.isConnected()) {
	
					if (zaehler > 100) {
						return false;
					}
					Thread.sleep(50);
					zaehler++;
				}
				return true;
		} else {
			return true;
		}
	}
	
	protected boolean isEmpfangen() {
		return this.empfangen;
	}
	
	protected void setEmpfangen(boolean empf) {
		this.empfangen = empf;
	}
	
	protected Object getResult() {
		return this.resultOfCommunication;
	}
	
	protected Client getClient() {
		return this.client;
	}

}
