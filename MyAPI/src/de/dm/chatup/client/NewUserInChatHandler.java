package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;

public class NewUserInChatHandler {
	static NewUserInChatHandler instance = null;
	List<NewUserInChatEvent> allListener = new ArrayList<NewUserInChatEvent>();

	public static NewUserInChatHandler getInstance() {
		if(instance == null) {
			instance = new NewUserInChatHandler();
		}
		return instance;
	}
	
	public void addListener(NewUserInChatEvent evt) {
		this.allListener.add(evt);
	}
	
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

	private void notify(NewUserInChatEvent toNotify, final Contact c, final int chatID) {
		
		toNotify.reactOnNewUserInChat(c, chatID);
		
//		if(toNotify instanceof ContactActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
//			final ContactActivity ca = (ContactActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewUserInChat(c, chatID);
//				}
//			});
//		} else if (toNotify instanceof ChatActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
//			final ChatActivity ca = (ChatActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewUserInChat(c, chatID);
//				}
//			});
//		} else if (toNotify instanceof NewChatActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
//			final NewChatActivity ca = (NewChatActivity)toNotify;
//			ca.findViewById(R.id.list_contacts).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewUserInChat(c, chatID);
//				}
//			});
//		} 
	}

}
