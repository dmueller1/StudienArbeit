package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
public class NewChatHandler {
	static NewChatHandler instance = null;
	List<NewChatEvent> allListener = new ArrayList<NewChatEvent>();

	public static NewChatHandler getInstance() {
		if(instance == null) {
			instance = new NewChatHandler();
		}
		return instance;
	}
	
	public void addListener(NewChatEvent evt) {
		this.allListener.add(evt);
	}
	
	protected void notifyAllListener(final Chat c) {
		
		int myUserID = AppSystem.getInstance().getUserID();
		Contact me = AppSystem.getInstance().getUserFromID(myUserID);
		
		// Bin ich �berhaupt in dem Chat drin --> interessiert es mich???
		if(c.getUsers().contains(me)) {
			AppSystem.getInstance().getMyChats().add(c);
			for (int i = 0; i < this.allListener.size(); i++) {
				notify(allListener.get(i), c);
			}
		}
	}
	
	private void notify(NewChatEvent toNotify, final Chat c) {
		
		toNotify.reactOnNewChat(c);
		
//		if (toNotify instanceof ContactActivity) {
//			final ContactActivity ca = (ContactActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewChat(c);
//				}
//			});
//		} else if (toNotify instanceof ChatActivity) {
//			final ChatActivity ca = (ChatActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewChat(c);
//				}
//			});
//		} else if (toNotify instanceof NewChatActivity) {
//			final NewChatActivity ca = (NewChatActivity)toNotify;
//			ca.findViewById(R.id.list_contacts).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewChat(c);
//				}
//			});
//		} 
		
	}
	
}
