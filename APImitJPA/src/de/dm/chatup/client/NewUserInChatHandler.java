package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;

/**
 * Handler, in dem die h�renden Klassen registriert werden und der das Ereignis ausl�st
 * @author Daniel M�ller
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
	 * F�gt eine Listener-Klasse, die das Event implementiert hinzu
	 * @param evt Die hinzuzuf�gende Klasse
	 */
	public void addListener(NewUserInChatEvent evt) {
		this.allListener.add(evt);
	}
	
	/**
	 * Methode, die das Ereignis ausl�st und alle registrierten Klassen darauf reagieren l�sst
	 * @param c Der Benutzer auf den reagiert werden soll
	 * @param chatID Die ID des Chats, dem dieser Benutzer hinzugef�gt wurde
	 */
	protected void notifyAllListener(final Contact c, final int chatID) {
		Chat isOneOfMyChats = AppSystem.getInstance().getChatFromID(chatID);
		if(isOneOfMyChats != null) {
			//F�ge UserID per ChatID zum Chat hinzu
			AppSystem.getInstance().addUserToChat(c.getUserID(), chatID);
			for (int i = 0; i < this.allListener.size(); i++) {
				notify(allListener.get(i), c, chatID);
			}
		}
	}

	/**
	 * Ruft die implementierte Methode der Listener-Klasse auf
	 * @param toNotify Die Listener-Klasse, in der die Methode implementiert wurde
	 * @param c Der hinzugef�gte Benutzer
	 * @param chatID Die ID des Chats, dem der Benutzer hinzugef�gt wurde
	 */
	private void notify(NewUserInChatEvent toNotify, final Contact c, final int chatID) {
		toNotify.reactOnNewUserInChat(c, chatID);
	}

}
