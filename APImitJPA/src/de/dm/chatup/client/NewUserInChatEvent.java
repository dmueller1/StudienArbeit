package de.dm.chatup.client;

import de.dm.chatup.network.Network.Contact;

/**
 * Interface, das in allen Klassen, die auf das Hinzufügen eines Benutzers zum Chat reagieren sollen, implementiert werden muss
 * @author Daniel Müller
 *
 */
	
public interface NewUserInChatEvent {
	
	/**
	 * Zu überschreibende Methode, die beim Hinzufügen eines neuen Mitglieds zum Chat, aufgerufen werden soll
	 * @param c Der hinzugefügte Benutzer
	 * @param chatID Die ID des Chats, dem der Benutzer hinzugefügt wurde
	 */
	public void reactOnNewUserInChat(Contact c, int chatID);

}
