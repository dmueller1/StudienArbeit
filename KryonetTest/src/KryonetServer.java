

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class KryonetServer {

	Server server;
	ServerConnection serverConn = ServerConnection.getInstance();

	public static void main(String[] args) {
		KryonetServer ks = new KryonetServer();
	}
	
	public KryonetServer() {
		try {
			server = new Server();
			Network.register(server);
			server.addListener(new Listener() {
				public void received(Connection connection, Object object) {
					
					if (object instanceof Network.IsUserExisting) {
						System.out.println("Request: Is User Registered?");
						Network.IsUserExisting iur = (Network.IsUserExisting)object;
						iur.result = serverConn.isUserRegistered(iur.deviceID);
						System.out.println(" --> UserID: " + iur.result);
						connection.sendTCP(iur);
						return;
						
					} else if (object instanceof Network.AddUser) {
						System.out.println("Request: Add new user...");
						Network.AddUser anu = (Network.AddUser)object; 
						anu.result = serverConn.addNewUser(anu.deviceID, anu.vorname, anu.nachname);
						System.out.println(" --> Result (UserID): " + anu.result);

						if(anu.result > -1) {
							Connection[] conns = server.getConnections();
							for(int i = 0; i < conns.length; i++) {
								conns[i].sendTCP(anu);
							}
						}
						return;
						
					} else if (object instanceof Network.GetFriends) {
						System.out.println("Request: Get friends...");
						Network.GetFriends gf = (Network.GetFriends)object;
						gf.result = serverConn.getFriends();
						System.out.println(" --> Data received (Array of friends): " + gf.result);
						connection.sendTCP(gf);
						return;
						
					} else if(object instanceof Network.GetChatsFromUser) {
						System.out.println("Request: Get chats from userID...");
						Network.GetChatsFromUser gc = (Network.GetChatsFromUser)object;
						gc.result = serverConn.getChatsFromUser(gc.userID);
						System.out.println(" --> Data received (Array of Chats): " + gc.result);
						connection.sendTCP(gc);
						return;
						
					}else if(object instanceof Network.GetUserIDsFromChat) {
						System.out.println("Request: Get Users in Chat...");
						Network.GetUserIDsFromChat gu = (Network.GetUserIDsFromChat)object;
						gu.result = serverConn.getUserIDsFromChat(gu.chatID);
						System.out.println(" --> Data received (Array of USerIDs): " + gu.result);
						connection.sendTCP(gu);
						return;
						
					} else if (object instanceof Network.GetAllMessagesFromChat) {
						System.out.println("Request: Get all messages...");
						Network.GetAllMessagesFromChat gam = (Network.GetAllMessagesFromChat)object;
						gam.result = serverConn.getAllMessagesFromChat(gam.chatID, gam.lastUpdate);
						System.out.println(" --> Data received (Array of messages):" + gam.result.length);
						connection.sendTCP(gam);
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
						int response = serverConn.sendNewMessage(snm.chatID, snm.erstellerID, snm.erstellDatum, snm.nachricht);
						System.out.println(" --> Message sent: " + response + " am " + datum);
						if(response > -1) {
							Connection[] conns = server.getConnections();
							for(int i = 0; i < conns.length; i++) {
								conns[i].sendTCP(snm);
							}
						}
						return;
						
					} else if (object instanceof Network.AddNewChat) {
						System.out.println("Request: Add new chat...");
						Network.AddNewChat anc = (Network.AddNewChat)object; 
						anc.result = serverConn.createNewChat(anc.name, anc.userIDs);
						System.out.println(" --> Data received(ChatID): " + anc.result);
						if(anc.result > -1) {
							Connection[] conns = server.getConnections();
							for(int i = 0; i < conns.length; i++) {
								conns[i].sendTCP(anc);
							}
						}
						return;
						
					} else if (object instanceof Network.AddUserToChat) {
						System.out.println("Request: Add user to chat...");
						Network.AddUserToChat autc = (Network.AddUserToChat)object; 
						int result = serverConn.addUserToChat(autc.chatID, String.valueOf(autc.userID));
						System.out.println(" --> Result: " + result);
						if(result > -1) {
							Connection[] conns = server.getConnections();
							for(int i = 0; i < conns.length; i++) {
								conns[i].sendTCP(autc);
							}
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

			server.bind(Network.port);
			server.start();
			System.out.println("Server erfolgreich gestartet auf Port " + Network.port + "; Adresse: " + InetAddress.getLocalHost().getHostAddress());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
