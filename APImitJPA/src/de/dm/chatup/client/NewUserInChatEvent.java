package de.dm.chatup.client;

import de.dm.chatup.network.Network.Contact;

/**
 * Interface, das in allen Klassen, die auf das Hinzuf�gen eines Benutzers zum Chat reagieren sollen, implementiert werden muss
 * @author Daniel M�ller
 *
 */
	
public interface NewUserInChatEvent {
	
	/**
	 * Zu �berschreibende Methode, die beim Hinzuf�gen eines neuen Mitglieds zum Chat, aufgerufen werden soll
	 * @param c Der hinzugef�gte Benutzer
	 * @param chatID Die ID des Chats, dem der Benutzer hinzugef�gt wurde
	 */
	public void reactOnNewUserInChat(Contact c, int chatID);

}
