package de.dm.chatup.client;

import de.dm.chatup.network.Network.Chat;

/**
 * Interface, das in allen Klassen, die auf das Erstellen eines neuen Chats reagieren sollen, implementiert werden muss
 * @author Daniel M�ller
 *
 */
public interface NewChatEvent {
	
	/**
	 * Zu �berschreibende Methode, die beim Eintreffen des Ereignisses aufgerufen wird
	 * @param c Der neu erstellte Chat, der in der Methode ausgewertet werden kann
	 */
	public void reactOnNewChat(Chat c);

}
