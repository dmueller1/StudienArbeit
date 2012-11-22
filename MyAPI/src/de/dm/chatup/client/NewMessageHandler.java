package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Message;


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

	protected void notifyAllListener(final Message msg) {
		int chatIdOfMessage = msg.getChatID();
		Chat isOneOfMyChats = AppSystem.getInstance().getChatFromID(chatIdOfMessage);
		AppSystem.getInstance().setLastMessage(msg);
		
		if(isOneOfMyChats != null) {
			AppSystem.getInstance().addMessageToChat(msg);
			for (int i = 0; i < this.allListener.size(); i++) {
				notify(allListener.get(i), msg);
			}
		}
	}

	private void notify(NewMessageEvent toNotify, final Message msg) {
		
		toNotify.reactOnNewMessage(msg);
		
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
