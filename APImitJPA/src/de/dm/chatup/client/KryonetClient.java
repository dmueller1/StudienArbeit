package de.dm.chatup.client;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import de.dm.chatup.network.Network;
import de.dm.chatup.network.Network.*;

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
	
	protected KryonetClient(final String serverLink, final int port) {
		
		try {
			this.empfangen = false;
			client = new Client(100000, 100000);
			client.start();
			Network.register(client);
			

			client.addListener(new Listener() {
			
				@Override
				public void disconnected(final Connection conn) {
					
	                new Thread() {
	                	public void run () {
	                    	try {
	                        	while(!client.isConnected()) {
	                            	client.reconnect(5000);
	                            }
	                        } catch (IOException ex) {
	                            ex.printStackTrace();
	                            try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
	                            disconnected(conn);
	                        }
	                    }
	                }.start();
					
				}

				public void received(Connection connection, Object object) {
					
					
					setEmpfangen(false);
					resultOfCommunication = object;
					
					if (object instanceof Network.AddUser
							&& ((Network.AddUser) object).result != null) {
						Contact c = ((Network.AddUser) object).result;
						setEmpfangen(true);
						NewUserHandler.getInstance().notifyAllListener(c);

					} else if (object instanceof Network.SendNewMessage) {
						Chat chat = AppSystem.getInstance().getChatFromID(((Network.SendNewMessage) object).chatID);
						Contact ersteller = AppSystem.getInstance().getUserFromID(((Network.SendNewMessage) object).erstellerID);
						
						Message m = new Message(chat, ersteller, ((Network.SendNewMessage) object).erstellDatum, ((Network.SendNewMessage) object).nachricht);
						setEmpfangen(true);
						NewMessageHandler.getInstance().notifyAllListener(chat, m);
						
					} else if (object instanceof Network.AddNewChat) {
						Chat c = ((Network.AddNewChat)object).result;
						if(c.getUsers() != null) {
							setEmpfangen(true);
							NewChatHandler.getInstance().notifyAllListener(c);
						}
						
					} else if (object instanceof Network.AddUserToChat) {
						Contact c = AppSystem.getInstance().getUserFromID(Integer.valueOf(((Network.AddUserToChat)object).userID));
						NewUserInChatHandler.getInstance().notifyAllListener(c, Integer.valueOf(((Network.AddUserToChat)object).chatID));
					
					} else if (object instanceof Network.IsUserExisting) {
					} else if (object instanceof Network.GetFriends) {
					} else if (object instanceof Network.GetChatsFromUser) {
//					} else if (object instanceof Network.GetUsersFromChat) {
//					} else if (object instanceof Network.GetAllMessagesFromChat) {
					} else if (object instanceof Network.GetDateFromServer) {
					} else if (object instanceof Network.AddDeviceToUser) {
					} else {
						setEmpfangen(false);
						resultOfCommunication = null;
						return;
					}
					
					setEmpfangen(true);
					return;
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
	
//	protected boolean reconnectClient(String serverLink, int port) throws IOException, InterruptedException {
//		if(!client.isConnected()) {
//			client.connect(5000, serverLink, port);
//			
//				int zaehler = 0;
//				while (!client.isConnected()) {
//	
//					if (zaehler > 100) {
//						return false;
//					}
//					Thread.sleep(50);
//					zaehler++;
//				}
//				return true;
//		} else {
//			return true;
//		}
//	}
	
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
