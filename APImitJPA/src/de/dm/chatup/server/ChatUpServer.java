package de.dm.chatup.server;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Message;
import de.dm.chatup.network.Network;

/**
 * Schnittstellen-Klasse des Servers, über welche zwischen Applikation und Chat-Logik kommuniziert werden kann
 * @author Daniel Müller
 *
 */
public class ChatUpServer {
	
	boolean isRunning;
	Server server;
	ServerConnection serverConn;
	
	/**
	 * Erstellt eine Server-Instanz
	 * @param port Port, auf den der Server hören soll
	 * @param serverLink Link zum Datenbankserver
	 * @param username Benutzername auf dem Datenbankserver
	 * @param password Passwort auf dem Datenbankserver
	 * @param database Datenbankname auf dem Datenbankserver
	 */
	public ChatUpServer (int port, String serverLink, String username, String password, String database) {
		
		try {
			this.serverConn = ServerConnection.getInstance(serverLink, username, password, database);
			this.isRunning = false;
			this.server = new Server(100000, 100000);
			Network.register(server);
			
			server.addListener(new Listener() {
				
				/**
				 * Methode, die aufgerufen wird, wenn der Server Daten empfängt. In ihr wird auf die eintreffenden Paketklassen reagiert und ggf. Antwortdaten zurückgesendet
				 */
				@SuppressWarnings("deprecation")
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
						try {
							anu.result = serverConn.addNewUser(anu.deviceID, anu.vorname, anu.nachname);
							System.out.println(" --> Result (UserID): " + anu.result);

							if(anu.result != null) {
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
							System.out.println(" --> Data received (Array of friends): " + gf.result.size());
							connection.sendTCP(gf);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if(object instanceof Network.GetChatsFromUser) {
						System.out.println("Request: Get chats from userID...");
						Network.GetChatsFromUser gc = (Network.GetChatsFromUser)object;
						try {
							gc.result = serverConn.getChatsFromUserID(gc.userID);
							System.out.println(" --> Data received (Array of Chats): " + gc.result);
							connection.sendTCP(gc);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						
						return;
						
					} else if (object instanceof Network.SendNewMessage) {
						System.out.println("Request: Send new message...");
						Date now = new Date();
						Timestamp tsNow = new Timestamp(now.getYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes(), now.getSeconds(), 0);
						Network.SendNewMessage snm = (Network.SendNewMessage)object;
						snm.erstellDatum = tsNow.toString();
						try {
							Message msg = serverConn.sendNewMessage(snm.chatID, snm.erstellerID, snm.erstellDatum, snm.nachricht);
							System.out.println(" --> Message sent: " + msg.nachricht + " am " + msg.erstellDatum);
							if(msg != null) {
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
							Chat temp = serverConn.createNewChat(anc.name);
							System.out.println(" --> Data received(ChatID): " + temp.chatid);
							
							if(anc.users != null) {
								for(int i = 0; i < anc.users.size(); i++) {
									temp = serverConn.addUserToChat(temp.chatid, anc.users.get(i).userid);
									System.out.println(" --> User Added To Chat: " + anc.users.get(i).userid);
								}
							}

							if(temp != null) {
								System.out.println(" --> User count on chat: " + temp.users.size());
								Connection[] conns = server.getConnections();
								anc.result = temp;
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
						try {
							Chat chat = serverConn.addUserToChat(autc.chatID, autc.userID);
							System.out.println(" --> Result: " + chat.chatid);
							if(chat != null) {
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
						Timestamp tsNow = new Timestamp(now.getYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes(), now.getSeconds(), 0);
						gdfs.result = tsNow.toString();
						System.out.println(" --> Result: " + gdfs.result);
						connection.sendTCP(gdfs);
						return;
					} else if (object instanceof Network.AddDeviceToUser) {
						System.out.println("Request: Add device to user...");
						Network.AddDeviceToUser adtu = (Network.AddDeviceToUser)object; 
						try {
							adtu.result = serverConn.addDeviceToUser(adtu.oldDeviceID, adtu.newDeviceID);
							System.out.println(" --> Result: " + adtu.result);
							connection.sendTCP(adtu);
						} catch (ServerActionErrorException e) {
							System.out.println(e.getMessage());
						}
						return;
					} else {
						System.out.println("..."+server.getConnections().length);
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
	
	/**
	 * Gibt eine kurze Rückmeldung zum aktuellen Status des Servers
	 * @return Text, der aussagt, ob der Server läuft oder nicht
	 */
	public String getStatus() {
		if(this.isRunning == true) {
				return "Server erfolgreich gestartet";
		} else {
			return "Server läuft nicht!";
		}
	}

}
