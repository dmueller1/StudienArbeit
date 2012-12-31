package de.dm.chatup.client;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Message;

/**
 * Interface, das in allen Klassen, die auf das Senden einer neuen Nachricht reagieren sollen, implementiert werden muss
 * @author Daniel Müller
 *
 */
public interface NewMessageEvent {
		
		/**
		 * Zu überschreibende Methode, die beim Eintreffen einer neuen Nachricht aufgerufen wird
		 * @param chat Der Chat, in dem die Nachricht erstellt wurde
		 * @param m Die gesendete Nachricht
		 */
	public void reactOnNewMessage(Chat chat, Message m);

}
