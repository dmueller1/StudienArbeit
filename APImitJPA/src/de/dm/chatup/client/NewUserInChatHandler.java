package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;

/**
 * Handler, in dem die hörenden Klassen registriert werden und der das Ereignis auslöst
 * @author Daniel Müller
 *
 */
public class NewUserInChatHandler {
	static NewUserInChatHandler instance = null;
	List<NewUserInChatEvent> allListener = new ArrayList<NewUserInChatEvent>();

	public static NewUserInChatHandler getInstance() {
		if(instance == null) {
			instance = new NewUserInChatHandler();
		}
		return instance;
	}
	
	/**
	 * Fügt eine Listener-Klasse, die das Event implementiert hinzu
	 * @param evt Die hinzuzufügende Klasse
	 */
	public void addListener(NewUserInChatEvent evt) {
		this.allListener.add(evt);
	}
	
	/**
	 * Methode, die das Ereignis auslöst und alle registrierten Klassen darauf reagieren lässt
	 * @param c Der Benutzer auf den reagiert werden soll
	 * @param chatID Die ID des Chats, dem dieser Benutzer hinzugefügt wurde
	 */
	protected void notifyAllListener(final Contact c, final int chatID) {
		Chat isOneOfMyChats = AppSystem.getInstance().getChatFromID(chatID);
		if(isOneOfMyChats != null) {
			//Füge UserID per ChatID zum Chat hinzu
			AppSystem.getInstance().addUserToChat(c.getUserID(), chatID);
			for (int i = 0; i < this.allListener.size(); i++) {
				notify(allListener.get(i), c, chatID);
			}
		}
	}

	/**
	 * Ruft die implementierte Methode der Listener-Klasse auf
	 * @param toNotify Die Listener-Klasse, in der die Methode implementiert wurde
	 * @param c Der hinzugefügte Benutzer
	 * @param chatID Die ID des Chats, dem der Benutzer hinzugefügt wurde
	 */
	private void notify(NewUserInChatEvent toNotify, final Contact c, final int chatID) {
		toNotify.reactOnNewUserInChat(c, chatID);
	}

}
