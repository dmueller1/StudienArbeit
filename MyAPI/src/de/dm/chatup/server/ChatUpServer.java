package de.dm.chatup.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import de.dm.chatup.network.Network;

public class ChatUpServer {
	
	boolean isRunning;
	Server server;
	ServerConnection serverConn;
	
	public static void main(String[] args) {
		
	}
	
	public ChatUpServer (int port, String serverLink, String username, String password, String database) {
		
		try {
			this.serverConn = ServerConnection.getInstance(port, serverLink, username, password, database);
			this.isRunning = false;
			this.server = new Server();
			Network.register(server);
			
			server.addListener(new Listener() {
				public void received(Connection connection, Object object) {
					
					if (object instanceof Network.IsUserExisting) {
						System.out.println("Request: Is User Registered?");
						Network.IsUserExisting iur = (Network.IsUserExisting)object;
						try {
							iur.result = serverConn.isUserRegistered(iur.deviceID);
							System.out.println(" --> UserID: " + iur.result);
							connection.sendTCP(iur);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if (object instanceof Network.AddUser) {
						System.out.println("Request: Add new user...");
						Network.AddUser anu = (Network.AddUser)object; 
						try {
							anu.result = serverConn.addNewUser(anu.deviceID, anu.vorname, anu.nachname);
							System.out.println(" --> Result (UserID): " + anu.result);

							if(anu.result > -1) {
								Connection[] conns = server.getConnections();
								for(int i = 0; i < conns.length; i++) {
									conns[i].sendTCP(anu);
								}
							}
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if (object instanceof Network.GetFriends) {
						System.out.println("Request: Get friends...");
						Network.GetFriends gf = (Network.GetFriends)object;
						try {
							gf.result = serverConn.getFriends();
							System.out.println(" --> Data received (Array of friends): " + gf.result);
							connection.sendTCP(gf);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if(object instanceof Network.GetChatsFromUser) {
						System.out.println("Request: Get chats from userID...");
						Network.GetChatsFromUser gc = (Network.GetChatsFromUser)object;
						try {
							gc.result = serverConn.getChatsFromUser(gc.userID);
							System.out.println(" --> Data received (Array of Chats): " + gc.result);
							connection.sendTCP(gc);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					}else if(object instanceof Network.GetUserIDsFromChat) {
						System.out.println("Request: Get Users in Chat...");
						Network.GetUserIDsFromChat gu = (Network.GetUserIDsFromChat)object;
						try {
							gu.result = serverConn.getUserIDsFromChat(gu.chatID);
							System.out.println(" --> Data received (Array of USerIDs): " + gu.result);
							connection.sendTCP(gu);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if (object instanceof Network.GetAllMessagesFromChat) {
						System.out.println("Request: Get all messages...");
						Network.GetAllMessagesFromChat gam = (Network.GetAllMessagesFromChat)object;
						try {
							gam.result = serverConn.getAllMessagesFromChat(gam.chatID, gam.lastUpdate);
							System.out.println(" --> Data received (Array of messages):" + gam.result.length);
							connection.sendTCP(gam);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if (object instanceof Network.SendNewMessage) {
						System.out.println("Request: Send new message...");
						Date now = new Date();
						String datum = String.valueOf(1900 + now.getYear()) + "-"
								+ String.valueOf(now.getMonth() + 1) + "-"
								+ String.valueOf(now.getDate()) + " "
								+ String.valueOf(now.getHours()) + ":"
								+ String.valueOf(now.getMinutes()) + ":"
								+ String.valueOf(now.getSeconds());
						Network.SendNewMessage snm = (Network.SendNewMessage)object;
						snm.erstellDatum = datum;
						int response;
						try {
							response = serverConn.sendNewMessage(snm.chatID, snm.erstellerID, snm.erstellDatum, snm.nachricht);
							System.out.println(" --> Message sent: " + response + " am " + datum);
							if(response > -1) {
								Connection[] conns = server.getConnections();
								for(int i = 0; i < conns.length; i++) {
									conns[i].sendTCP(snm);
								}
							}
							return;
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
					} else if (object instanceof Network.AddNewChat) {
						System.out.println("Request: Add new chat...");
						Network.AddNewChat anc = (Network.AddNewChat)object; 
						try {
							anc.result = serverConn.createNewChat(anc.name, anc.userIDs);
							System.out.println(" --> Data received(ChatID): " + anc.result);
							if(anc.result > -1) {
								Connection[] conns = server.getConnections();
								for(int i = 0; i < conns.length; i++) {
									conns[i].sendTCP(anc);
								}
							}
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if (object instanceof Network.AddUserToChat) {
						System.out.println("Request: Add user to chat...");
						Network.AddUserToChat autc = (Network.AddUserToChat)object; 
						int result;
						try {
							result = serverConn.addUserToChat(autc.chatID, String.valueOf(autc.userID));
							System.out.println(" --> Result: " + result);
							if(result > -1) {
								Connection[] conns = server.getConnections();
								for(int i = 0; i < conns.length; i++) {
									conns[i].sendTCP(autc);
								}
							}
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						return;
					} else if (object instanceof Network.GetDateFromServer) {
						System.out.println("Request: Get date from Server...");
						Network.GetDateFromServer gdfs = (Network.GetDateFromServer)object; 
						
						Date now = new Date();
						String datum = String.valueOf(1900 + now.getYear()) + "-"
								+ String.valueOf(now.getMonth() + 1) + "-"
								+ String.valueOf(now.getDate()) + " "
								+ String.valueOf(now.getHours()) + ":"
								+ String.valueOf(now.getMinutes()) + ":"
								+ String.valueOf(now.getSeconds());
						gdfs.result = datum;
						System.out.println(" --> Result: " + gdfs.result);
						connection.sendTCP(gdfs);
						return;
					} else {
						System.out.println("...");
						return;
					}
				}
			});

			server.bind(port);
			server.start();
			this.isRunning = true;
			System.out.println();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
	}
	
	public String getStatus() {
		if(this.isRunning == true) {
			try {
				return "Server erfolgreich gestartet auf Port " + serverConn.port + "; Adresse: " + InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return "Fehler beim Abfragen des Status!";
			}
		} else {
			return "Server läuft nicht!";
		}
	}

}
