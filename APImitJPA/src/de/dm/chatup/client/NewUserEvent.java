package de.dm.chatup.client;

import de.dm.chatup.network.Network.Contact;
/**
 * Interface, das in allen Klassen, die auf das Registrieren eines neuen Benutzers reagieren sollen, implementiert werden muss
 * @author Daniel Müller
 *
 */

public interface NewUserEvent {
	
	/**
	 * Zu überschreibende Methode, die beim Registrieren eines neuen Benutzers aufgerufen werden soll
	 * @param c Der neu registrierte Benutzer
	 */
	public void reactOnNewUser(Contact c);

}
