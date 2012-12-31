package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.network.Network.Contact;

/**
 * Handler, in dem die h�renden Klassen registriert werden und der das Ereignis ausl�st
 * @author Daniel M�ller
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
	 * F�gt eine Listener-Klasse, die das Event implementiert hinzu
	 * @param evt Die hinzuzuf�gende Klasse
	 */
	public void addListener(NewUserEvent evt) {
		this.allListener.add(evt);
	}
	
	/**
	 * Methode, die das Ereignis ausl�st und alle registrierten Klassen darauf reagieren l�sst
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
