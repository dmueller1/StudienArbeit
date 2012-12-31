package de.dm.chatup.client;

import java.util.List;
import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;
import de.dm.chatup.network.Network;

/**
 * Schnittstellen-Klasse des Clienten, über welche zwischen Applikation und Chat-Logik kommuniziert werden kann
 * @author Daniel Müller
 *
 */
public class ChatUpClient {
	
	AppSystemOnline appSystemOnline;
	static ChatUpClient instance = null;
	
	protected ChatUpClient(String serverLink, int port) {
		appSystemOnline = AppSystemOnline.getInstance(serverLink, port);
	}
	
	/**
	 * Erstellt bzw. holt die Instanz des Clienten
	 * @param serverLink Link, über welchen der Server erreichbar ist
	 * @param port Port, auf den der Client hören muss
	 * @return Die Instanz des Clienten
	 */
	public static ChatUpClient getInstance(String serverLink, int port) {
		if(instance==null) {
			instance = new ChatUpClient(serverLink, port);
		}
		return instance;
	}
	
	/**
	 * Prüft, ob die übergebene Geräte-Kennung bereits im Datenbankserver registriert ist, also dem Gerät bereits ein Benutzer zugewiesen wurde.
	 * @param deviceID Geräte-ID des Endgeräts
	 * @return Die Benutzer-ID, falls das Gerät bereits auf einen Benutzer registriert wurde (>0) oder Anzeigen eines Misserfolgs (-1)
	 * @throws ClientNotConnectedException Fehler, der geworfen wird, wenn das Abfragen der Daten fehlschlägt (z.B. Timeout)
	 */
	public int anmelden(String deviceID) throws ClientNotConnectedException {
		int userID = appSystemOnline.isUserExisting(deviceID);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}

	/**
	 * Weist einem Benutzer ein weiteres Gerät zu
	 * @param oldDeviceID ID eines bereits auf den Benutzer angemeldetes Endgerät
	 * @param newDeviceID ID des hinzuzufügenden Geräts
	 * @return UserID des Benutzers, dem das Gerät hinzugefügt wurde
	 * @throws ClientNotConnectedException Fehler, der geworfen wird, wenn das Hinzufügen des neuen Geräts fehlschlägt (z.B. durch Timeout)
	 */
	public int addDeviceToUser(String oldDeviceID, String newDeviceID) throws ClientNotConnectedException {
		
		int userID = appSystemOnline.addDeviceToUser(oldDeviceID, newDeviceID);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}

	/**
	 * Erstellt einen neuen Systembenutzer mithilfe der Angabe von Namen und Device-ID
	 * @param deviceID Geräte-ID des Mobilgeräts
	 * @param firstname Vorname des Benutzers
	 * @param lastname Nachname des Benutzers
	 * @return Die User-ID des neu erstellen Benutzers
	 * @throws ClientUserAddingErrorException Fehler, der geworfen wird, wenn das Anlegen des neuen Benutzers fehlschlägt (z.B. durch Timeout)
	 */
	public int registerNewUser(String deviceID, String firstname,
			String lastname) throws ClientUserAddingErrorException {
		int userID = appSystemOnline.createNewUser(deviceID, firstname, lastname);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}
	
	/**
	 * Diese Methode holt sich alle initialen Daten vom Server --> Benutzer und Chats
	 */
	public void getInitialChatDataFromServer() {
		appSystemOnline.getInitialData();
	}
	
//	public void writeMessagesAndLastUpdateFile(String lastUpdateFilePath, String messagesFilePath) {
//		//appSystemOnline.getAppSystem().writeMessagesToFile(lastUpdateFilePath, messagesFilePath);
//	}

	/**
	 * Senden einer neuen Nachricht
	 * @param message Nachrichtentext, der gesendet werden soll.
	 * @throws ClientMessageSendErrorException Fehler, der geworfen wird, wenn das Senden der Nachricht fehlschlägt (z.B. durch Timeout)
	 */
	public void sendNewMessage(String message) throws ClientMessageSendErrorException {
		appSystemOnline.sendNewMessage(message);
	}

	/**
	 * Erstellt mithilfe einer Paketklasse einen neuen Chat (Server erstellt aus dessen Angaben einen entsprechenden Datenbankeintrag)
	 * @param anc Die Paketklasse vom Typ Network.AddNewChat, ausgefüllt mit Angaben zum anzulegenden Chat
	 * @throws ClientCreateChatErrorException Fehler, der geworfen wird, wenn das Erstellen des Chats fehlschlägt (z.B. durch Timeout)
	 */
	public void addNewChat(Network.AddNewChat anc) throws ClientCreateChatErrorException {
		appSystemOnline.createNewChat(anc);
	}

	/**
	 * Fügt einen Benutzer zu einem bestimmten Chat hinzu
	 * @param chatID Die ID des Chats, dem der Benutzer hinzugefügt werden soll
	 * @param userID Die ID des Benutzers, der dem Chat hinzugefügt werden soll
	 * @throws ClientAddUserToChatErrorException Fehler, der geworfen wird, wenn das Hinzufügen des Benutzers fehlschlägt (z.B. durch Timeout)
	 */
	public void addUserToChat(int chatID, int userID) throws ClientAddUserToChatErrorException {
		appSystemOnline.addUserToChat(chatID, userID);
	}
	
	/**
	 * Gibt alle im System vorhandenen Benutzer zurück (funktioniert erst, nachdem das System über die Methode getInitialChatDataFromServer gefüllt wurde)
	 * @return Liste aller zum aktuellen Zeitpunkt im System vorhandenen Benutzer
	 */
	public List<Contact> getAllContactsInSystem() {
		return appSystemOnline.getAppSystem().getMyContacts();
	}
	
	/**
	 * Gibt alle im System vorhandenen Chats, bei denen der angemeldete Benutzer Mitglied ist, zurück (funktioniert erst, nachdem der Benutzer über die Methode anmelden angemeldet und das System über die Methode getInitialChatDataFromServer gefüllt wurde)
	 * @return Liste der zum aktuellen Zeitpunkt interssanten Chats des Benutzers
	 */
	public List<Chat> getAllMyChats() {
		return appSystemOnline.getAppSystem().getMyChats();
	}
	
	/**
	 * Liest den zur ID gehörenden Benutzer aus dem System aus und gibt ihn zurück
	 * @param userID Die Kennung des gesuchten Benutzers
	 * @return Benutzer-Instanz des gesuchten Benutzers
	 */
	public Contact getUserFromID(int userID) {
		return appSystemOnline.getAppSystem().getUserFromID(userID);
	}
	
//	public void addMessageToChat(Message m, Chat c) {
//		appSystemOnline.getAppSystem().addMessageToChat(m, c);
//	}
	
	/**
	 * Liest den zur ID gehörenden Chat aus dem System aus und gibt diesen zurück
	 * @param chatID Die Kennung des zu suchenden Chats
	 * @return Chat-Instanz des gesuchten Chats
	 */
	public Chat getChatFromID(int chatID) {
		return appSystemOnline.getAppSystem().getChatFromID(chatID);
	}
	
	/**
	 * Gibt die zum angemeldeten Benutzer gehörende ID aus
	 * @return Die ID des angemeldeten Benutzers
	 */
	public int getMyUserID() {
		return appSystemOnline.getAppSystem().getUserID();
	}
	
	/**
	 * Liest die ID des aktuell geöffneten Chats aus
	 * @return Die Kennung des aktuell geöffneten Chats
	 */
	public int getActualChatID() {
		return appSystemOnline.getAppSystem().getOpenChat();
	}
	
	/**
	 * Setzt den aktuell in der Anwendung geöffneten Chat mithilfe der übergebenen ID
	 * @param chatID Die ID zum Chat
	 */
	public void setActualChatID(int chatID) {
		appSystemOnline.getAppSystem().setOpenChat(chatID);
	}
	
//	public boolean reconnectClient(String serverLink, int port) throws IOException, InterruptedException {
//		return appSystemOnline.getKryonetClient().reconnectClient(serverLink, port);
//	}
	
	/**
	 * Prüft, ob der Client momentan verbunden ist
	 * @return Boolean, ob der Client verbunden ist
	 */
//	public boolean isClientConnected() {
//		return appSystemOnline.getKryonetClient().getClient().isConnected();
//	}
}
