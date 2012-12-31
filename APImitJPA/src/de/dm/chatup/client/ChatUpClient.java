package de.dm.chatup.client;

import java.util.List;
import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;
import de.dm.chatup.network.Network;

/**
 * Schnittstellen-Klasse des Clienten, �ber welche zwischen Applikation und Chat-Logik kommuniziert werden kann
 * @author Daniel M�ller
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
	 * @param serverLink Link, �ber welchen der Server erreichbar ist
	 * @param port Port, auf den der Client h�ren muss
	 * @return Die Instanz des Clienten
	 */
	public static ChatUpClient getInstance(String serverLink, int port) {
		if(instance==null) {
			instance = new ChatUpClient(serverLink, port);
		}
		return instance;
	}
	
	/**
	 * Pr�ft, ob die �bergebene Ger�te-Kennung bereits im Datenbankserver registriert ist, also dem Ger�t bereits ein Benutzer zugewiesen wurde.
	 * @param deviceID Ger�te-ID des Endger�ts
	 * @return Die Benutzer-ID, falls das Ger�t bereits auf einen Benutzer registriert wurde (>0) oder Anzeigen eines Misserfolgs (-1)
	 * @throws ClientNotConnectedException Fehler, der geworfen wird, wenn das Abfragen der Daten fehlschl�gt (z.B. Timeout)
	 */
	public int anmelden(String deviceID) throws ClientNotConnectedException {
		int userID = appSystemOnline.isUserExisting(deviceID);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}

	/**
	 * Weist einem Benutzer ein weiteres Ger�t zu
	 * @param oldDeviceID ID eines bereits auf den Benutzer angemeldetes Endger�t
	 * @param newDeviceID ID des hinzuzuf�genden Ger�ts
	 * @return UserID des Benutzers, dem das Ger�t hinzugef�gt wurde
	 * @throws ClientNotConnectedException Fehler, der geworfen wird, wenn das Hinzuf�gen des neuen Ger�ts fehlschl�gt (z.B. durch Timeout)
	 */
	public int addDeviceToUser(String oldDeviceID, String newDeviceID) throws ClientNotConnectedException {
		
		int userID = appSystemOnline.addDeviceToUser(oldDeviceID, newDeviceID);
		appSystemOnline.getAppSystem().setUserID(userID);
		return userID;
	}

	/**
	 * Erstellt einen neuen Systembenutzer mithilfe der Angabe von Namen und Device-ID
	 * @param deviceID Ger�te-ID des Mobilger�ts
	 * @param firstname Vorname des Benutzers
	 * @param lastname Nachname des Benutzers
	 * @return Die User-ID des neu erstellen Benutzers
	 * @throws ClientUserAddingErrorException Fehler, der geworfen wird, wenn das Anlegen des neuen Benutzers fehlschl�gt (z.B. durch Timeout)
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
	 * @throws ClientMessageSendErrorException Fehler, der geworfen wird, wenn das Senden der Nachricht fehlschl�gt (z.B. durch Timeout)
	 */
	public void sendNewMessage(String message) throws ClientMessageSendErrorException {
		appSystemOnline.sendNewMessage(message);
	}

	/**
	 * Erstellt mithilfe einer Paketklasse einen neuen Chat (Server erstellt aus dessen Angaben einen entsprechenden Datenbankeintrag)
	 * @param anc Die Paketklasse vom Typ Network.AddNewChat, ausgef�llt mit Angaben zum anzulegenden Chat
	 * @throws ClientCreateChatErrorException Fehler, der geworfen wird, wenn das Erstellen des Chats fehlschl�gt (z.B. durch Timeout)
	 */
	public void addNewChat(Network.AddNewChat anc) throws ClientCreateChatErrorException {
		appSystemOnline.createNewChat(anc);
	}

	/**
	 * F�gt einen Benutzer zu einem bestimmten Chat hinzu
	 * @param chatID Die ID des Chats, dem der Benutzer hinzugef�gt werden soll
	 * @param userID Die ID des Benutzers, der dem Chat hinzugef�gt werden soll
	 * @throws ClientAddUserToChatErrorException Fehler, der geworfen wird, wenn das Hinzuf�gen des Benutzers fehlschl�gt (z.B. durch Timeout)
	 */
	public void addUserToChat(int chatID, int userID) throws ClientAddUserToChatErrorException {
		appSystemOnline.addUserToChat(chatID, userID);
	}
	
	/**
	 * Gibt alle im System vorhandenen Benutzer zur�ck (funktioniert erst, nachdem das System �ber die Methode getInitialChatDataFromServer gef�llt wurde)
	 * @return Liste aller zum aktuellen Zeitpunkt im System vorhandenen Benutzer
	 */
	public List<Contact> getAllContactsInSystem() {
		return appSystemOnline.getAppSystem().getMyContacts();
	}
	
	/**
	 * Gibt alle im System vorhandenen Chats, bei denen der angemeldete Benutzer Mitglied ist, zur�ck (funktioniert erst, nachdem der Benutzer �ber die Methode anmelden angemeldet und das System �ber die Methode getInitialChatDataFromServer gef�llt wurde)
	 * @return Liste der zum aktuellen Zeitpunkt interssanten Chats des Benutzers
	 */
	public List<Chat> getAllMyChats() {
		return appSystemOnline.getAppSystem().getMyChats();
	}
	
	/**
	 * Liest den zur ID geh�renden Benutzer aus dem System aus und gibt ihn zur�ck
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
	 * Liest den zur ID geh�renden Chat aus dem System aus und gibt diesen zur�ck
	 * @param chatID Die Kennung des zu suchenden Chats
	 * @return Chat-Instanz des gesuchten Chats
	 */
	public Chat getChatFromID(int chatID) {
		return appSystemOnline.getAppSystem().getChatFromID(chatID);
	}
	
	/**
	 * Gibt die zum angemeldeten Benutzer geh�rende ID aus
	 * @return Die ID des angemeldeten Benutzers
	 */
	public int getMyUserID() {
		return appSystemOnline.getAppSystem().getUserID();
	}
	
	/**
	 * Liest die ID des aktuell ge�ffneten Chats aus
	 * @return Die Kennung des aktuell ge�ffneten Chats
	 */
	public int getActualChatID() {
		return appSystemOnline.getAppSystem().getOpenChat();
	}
	
	/**
	 * Setzt den aktuell in der Anwendung ge�ffneten Chat mithilfe der �bergebenen ID
	 * @param chatID Die ID zum Chat
	 */
	public void setActualChatID(int chatID) {
		appSystemOnline.getAppSystem().setOpenChat(chatID);
	}
	
//	public boolean reconnectClient(String serverLink, int port) throws IOException, InterruptedException {
//		return appSystemOnline.getKryonetClient().reconnectClient(serverLink, port);
//	}
	
	/**
	 * Pr�ft, ob der Client momentan verbunden ist
	 * @return Boolean, ob der Client verbunden ist
	 */
//	public boolean isClientConnected() {
//		return appSystemOnline.getKryonetClient().getClient().isConnected();
//	}
}
