package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Message;

/**
 * Handler, in dem die h�renden Klassen registriert werden und der das Ereignis ausl�st
 * @author Daniel M�ller
 *
 */
public class NewMessageHandler {

	static NewMessageHandler instance = null;
	List<NewMessageEvent> allListener = new ArrayList<NewMessageEvent>();

	public static NewMessageHandler getInstance() {
		if (instance == null) {
			instance = new NewMessageHandler();
		}
		return instance;
	}

	/**
	 * F�gt eine Listener-Klasse, die das Event implementiert hinzu
	 * @param evt Die hinzuzuf�gende Klasse
	 */
	public void addListener(NewMessageEvent evt) {
		this.allListener.add(evt);
	}

	/**
	 * Methode, die das Ereignis ausl�st und alle registrierten Klassen darauf reagieren l�sst
	 * @param chat Der Chat, in dem die Nachricht erstellt wurde
	 * @param msg Die empfangene Nachricht
	 */
	protected void notifyAllListener(final Chat chat, final Message msg) {
		AppSystem.getInstance().setLastMessage(msg);
		
		for(int j = 0; j < AppSystem.getInstance().getMyChats().size(); j++) {
			if(AppSystem.getInstance().getMyChats().get(j).getChatID() == chat.getChatID()) {
				//AppSystem.getInstance().addMessageToChat(msg, chat);
				for (int i = 0; i < this.allListener.size(); i++) {
					notify(allListener.get(i), chat, msg);
				}
			}
		}
		
	}

	/**
	 * Ruft die implementierte Methode der Listener-Klasse auf
	 * @param toNotify Die Listener-Klasse, in der die Methode implementiert wurde
	 * @param chat Der zur Nachricht geh�rende Chat
	 * @param msg Die empfangene Nachricht
	 */
	private void notify(NewMessageEvent toNotify, final Chat chat, final Message msg) {
		toNotify.reactOnNewMessage(chat, msg);
	}
}
