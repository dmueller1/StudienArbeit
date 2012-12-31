package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.network.Network.Contact;

/**
 * Handler, in dem die hörenden Klassen registriert werden und der das Ereignis auslöst
 * @author Daniel Müller
 *
 */
public class NewUserHandler {

	static NewUserHandler instance = null;
	List<NewUserEvent> allListener = new ArrayList<NewUserEvent>();

	public static NewUserHandler getInstance() {
		if(instance == null) {
			instance = new NewUserHandler();
		}
		return instance;
	}
	
	/**
	 * Fügt eine Listener-Klasse, die das Event implementiert hinzu
	 * @param evt Die hinzuzufügende Klasse
	 */
	public void addListener(NewUserEvent evt) {
		this.allListener.add(evt);
	}
	
	/**
	 * Methode, die das Ereignis auslöst und alle registrierten Klassen darauf reagieren lässt
	 * @param c Der Benutzer auf den reagiert werden soll
	 */
	protected void notifyAllListener(Contact c) {
		AppSystem.getInstance().addContact(c);
		for (int i = 0; i < this.allListener.size(); i++) {
			notify(allListener.get(i), c);
		}
	}

	/**
	 * Ruft die implementierte Methode der Listener-Klasse auf
	 * @param toNotify Die Listener-Klasse, in der die Methode implementiert wurde
	 * @param c Der neu registrierte Benutzer
	 */
	private void notify(NewUserEvent toNotify, final Contact c) {
		toNotify.reactOnNewUser(c);
	}
}
