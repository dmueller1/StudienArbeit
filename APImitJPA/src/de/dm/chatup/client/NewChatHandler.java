package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;
import de.dm.chatup.network.Network.Chat;

/**
 * Handler, in dem die hörenden Klassen registriert werden und der das Ereignis auslöst
 * @author Daniel Müller
 *
 */
public class NewChatHandler {
	static NewChatHandler instance = null;
	List<NewChatEvent> allListener = new ArrayList<NewChatEvent>();

	public static NewChatHandler getInstance() {
		if(instance == null) {
			instance = new NewChatHandler();
		}
		return instance;
	}
	
	/**
	 * Fügt eine Listener-Klasse, die das Event implementiert hinzu
	 * @param evt Die hinzuzufügende Klasse
	 */
	public void addListener(NewChatEvent evt) {
		this.allListener.add(evt);
	}
	
	/**
	 * Methode, die das Ereignis auslöst und alle registrierten Klassen darauf reagieren lässt
	 * @param c Neu erstellter Chat, auf den reagiert werden soll
	 */
	protected void notifyAllListener(final Chat c) {
		
		int myUserID = AppSystem.getInstance().getUserID();
		
		// Bin ich überhaupt in dem Chat drin --> interessiert es mich???
		for(int i = 0; i < c.getUsers().size(); i++) {
			if(c.users.get(i).getUserID() == myUserID) {
				AppSystem.getInstance().getMyChats().add(c);
				for (int j = 0; j < this.allListener.size(); j++) {
					notify(allListener.get(j), c);
				}
				return;
			}
		}
	}
	
	/**
	 * Ruft die implementierte Methode der Listener-Klasse auf
	 * @param toNotify Die Listener-Klasse, in der die Methode implementiert wurde
	 * @param c Der neu erstellte Chat, auf den reagiert werden soll
	 */
	private void notify(NewChatEvent toNotify, final Chat c) {
		toNotify.reactOnNewChat(c);
	}
}
