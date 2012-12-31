package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;
import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;
import de.dm.chatup.client.KryonetClient;
import de.dm.chatup.network.Network;

/**
 * Diese Klasse ist für den Online-Teil der Logik zuständig - sie kommuniziert mit der KryonetClient-Klasse
 * @author Daniel Müller
 *
 */
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
	
	/**
	 * Diese Methode holt sich alle initialen Daten vom Server --> Benutzer und Chats
	 */
	protected void getInitialData() {
		
		//Bisherige Daten löschen
		appSystem.setMyContacts(new ArrayList<Contact>());
		appSystem.setMyChats(new ArrayList<Chat>());
		
		// 1. Schritt: Benutzer auslesen
		Network.GetFriends gf = new Network.GetFriends();
		if (kryonet.send(gf) && kryonet.getResult() instanceof Network.GetFriends
				&& ((Network.GetFriends)kryonet.getResult()).result != null) {
			List<Contact> benutzer = ((Network.GetFriends)kryonet.getResult()).result;
			
			if(benutzer != null) {
				for (int i = 0; i < benutzer.size(); i++) {
					appSystem.addContact(benutzer.get(i));
				}
			}

			// 2. Schritt: Chats auslesen
			Network.GetChatsFromUser guc = new Network.GetChatsFromUser();
			guc.userID = appSystem.getUserID();
			if (kryonet.send(guc) && kryonet.getResult() instanceof Network.GetChatsFromUser
					&& ((Network.GetChatsFromUser) kryonet.getResult()).result != null) {
				List<Chat> chats = ((Network.GetChatsFromUser) kryonet.getResult()).result;

				if(chats != null) {
					for (int i = 0; i < chats.size(); i++) {
						appSystem.addChat(chats.get(i));
					}
				}
			}
		}
	}
	
	/**
	 * Erstellt mithilfe einer Paketklasse einen neuen Chat (Server erstellt aus dessen Angaben einen entsprechenden Datenbankeintrag)
	 * @param anc Die Paketklasse vom Typ Network.AddNewChat, ausgefüllt mit Angaben zum anzulegenden Chat
	 * @throws ClientCreateChatErrorException Fehler, der geworfen wird, wenn das Erstellen des Chats fehlschlägt (z.B. durch Timeout)
	 */
	protected void createNewChat(Network.AddNewChat anc) throws ClientCreateChatErrorException {
		
		if (kryonet.send(anc) && kryonet.getResult() instanceof Network.AddNewChat
				&& ((Network.AddNewChat)kryonet.getResult()).result != null) {
			return;
			
		}
		throw new ClientCreateChatErrorException("Chat konnte nicht erstellt werden!");
	}
	
	/**
	 * Prüft, ob es zur übergebenen Geräte-ID bereits einen Eintrag in der Datenbank gibt und liest ggf. die User-ID des zugehörigen Benutzers aus
	 * @param deviceID Die Geräte-ID des mobilen Endgeräts
	 * @return Die User-ID des angemeldeten Benutzers (>0) oder Fehler, wenn kein Eintrag gefunden wurde (-1)
	 * @throws ClientNotConnectedException Fehler, der geworfen wird, wenn das Suchen der Device-ID fehlschlägt (z.B. durch Timeout)
	 */
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
	
	/**
	 * Erstellt einen neuen Systembenutzer mithilfe der Angabe von Namen und Device-ID
	 * @param deviceID Geräte-ID des Mobilgeräts
	 * @param vorname Vorname des Benutzers
	 * @param nachname Nachname des Benutzers
	 * @return Die User-ID des neu erstellen Benutzers
	 * @throws ClientUserAddingErrorException Fehler, der geworfen wird, wenn das Anlegen des neuen Benutzers fehlschlägt (z.B. durch Timeout)
	 */
	protected int createNewUser(String deviceID, String vorname, String nachname) throws ClientUserAddingErrorException {
		Network.AddUser au = new Network.AddUser();
		au.deviceID = deviceID;
		au.vorname = vorname;
		au.nachname = nachname;

		if (kryonet.send(au) && kryonet.getResult() instanceof Network.AddUser
				&& ((Network.AddUser)kryonet.getResult()).result != null) {
			au.result = ((Network.AddUser)kryonet.getResult()).result;
			appSystem.addContact(au.result);
			appSystem.setUserID(au.result.getUserID());
			return au.result.getUserID();
		}
		throw new ClientUserAddingErrorException("Benutzer konnte nicht hinzugefügt werden!");
	}
	
	/**
	 * Senden einer neuen Nachricht
	 * @param nachricht Nachrichtentext, der gesendet werden soll.
	 * @throws ClientMessageSendErrorException Fehler, der geworfen wird, wenn das Senden der Nachricht fehlschlägt (z.B. durch Timeout)
	 */
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
	
	/**
	 * Fügt einen Benutzer zu einem bestimmten Chat hinzu
	 * @param chatID Die ID des Chats, dem der Benutzer hinzugefügt werden soll
	 * @param userID Die ID des Benutzers, der dem Chat hinzugefügt werden soll
	 * @throws ClientAddUserToChatErrorException Fehler, der geworfen wird, wenn das Hinzufügen des Benutzers fehlschlägt (z.B. durch Timeout)
	 */
	protected void addUserToChat(int chatID, int userID) throws ClientAddUserToChatErrorException {
		
		Network.AddUserToChat autc = new Network.AddUserToChat();
		autc.chatID = chatID;
		autc.userID = userID;
		if(kryonet.send(autc)) {
			return;
		}
		throw new ClientAddUserToChatErrorException("Benutzer konnte nicht zum Chat hinzugefügt werden!");
	}
	
//	/**
//	 * Frägt die aktuelle Zeit am Server ab und gibt diese zurück
//	 * @return Die am Server aktuelle Zeit
//	 * @throws GetDateFromServerErrorException Fehler, der geworfen wird, wenn das Abfragen der Zeit am Server fehlschlägt (z.B. durch Timeout)
//	 */
//	protected String getDateFromServer() throws GetDateFromServerErrorException {
//		Network.GetDateFromServer gdfs = new Network.GetDateFromServer();
//		
//		if (kryonet.send(gdfs) && kryonet.getResult() instanceof Network.GetDateFromServer) {
//			String t = ((Network.GetDateFromServer)kryonet.getResult()).result;
//			return t;
//		} 
//		throw new GetDateFromServerErrorException("Fehler beim Abfragen der Zeit am Server");
//	}
	
	/**
	 * Weist einem Benutzer ein weiteres Gerät zu
	 * @param oldDeviceID ID eines bereits auf den Benutzer angemeldetes Endgerät
	 * @param newDeviceID ID des hinzuzufügenden Geräts
	 * @return UserID des Benutzers, dem das Gerät hinzugefügt wurde
	 * @throws ClientNotConnectedException Fehler, der geworfen wird, wenn das Hinzufügen des neuen Geräts fehlschlägt (z.B. durch Timeout)
	 */
	protected int addDeviceToUser(String oldDeviceID, String newDeviceID) throws ClientNotConnectedException  {
		Network.AddDeviceToUser adtu = new Network.AddDeviceToUser();
		adtu.oldDeviceID = oldDeviceID;
		adtu.newDeviceID = newDeviceID;
		
		if (kryonet.send(adtu) && kryonet.getResult() instanceof Network.AddDeviceToUser) {
			adtu.result =  ((Network.AddDeviceToUser)kryonet.getResult()).result;
			
			if(adtu.result > -1) {
				appSystem.setUserID(adtu.result);
				return adtu.result;
			} else {
				return -1;
			}
		} 

		throw new ClientNotConnectedException("Keine Antwort vom Server erhalten. Besteht eine Internetverbindung?");
	}
	
	/**
	 * Gibt die lokale Verwaltungsinstanz zurück
	 * @return Die Instanz der lokalen Logik
	 */
	protected AppSystem getAppSystem() {
		return this.appSystem;
	}
	
	/**
	 * Gibt die Instanz der Kryonet-Klasse zurück
	 * @return Die Instanz der Kryonet-Klasse
	 */
	protected KryonetClient getKryonetClient() {
		return this.kryonet;
	}

}
