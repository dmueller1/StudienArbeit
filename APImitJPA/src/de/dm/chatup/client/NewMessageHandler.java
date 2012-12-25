package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Message;


public class NewMessageHandler {

	static NewMessageHandler instance = null;
	List<NewMessageEvent> allListener = new ArrayList<NewMessageEvent>();

	public static NewMessageHandler getInstance() {
		if (instance == null) {
			instance = new NewMessageHandler();
		}
		return instance;
	}

	public void addListener(NewMessageEvent evt) {
		this.allListener.add(evt);
	}

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

	private void notify(NewMessageEvent toNotify, final Chat chat, final Message msg) {
		
		toNotify.reactOnNewMessage(chat, msg);
		
//		if(toNotify instanceof ContactActivity) { //&& AppSystem.getInstance().getUserID() != msg.getErstellerID()) {
//			final ContactActivity ca = (ContactActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					ca.reactOnNewMessage(msg);
//				}
//			});
//		} else if (toNotify instanceof ChatActivity) { //  && AppSystem.getInstance().getUserID() != msg.getErstellerID()) {
//			final ChatActivity ca = (ChatActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					ca.reactOnNewMessage(msg);			
//				}
//			});
//		} else if (toNotify instanceof NewChatActivity) { //  && AppSystem.getInstance().getUserID() != msg.getErstellerID()) {
//			final NewChatActivity ca = (NewChatActivity)toNotify;
//			ca.findViewById(R.id.list_contacts).post(new Runnable() {
//				public void run() {
//					ca.reactOnNewMessage(msg);
//				}
//			});
//		} 
	}
}
