package de.dm.chatup.client;

import java.util.ArrayList;
import java.util.List;

import de.dm.chatup.chat.Contact;

public class NewUserHandler {

	static NewUserHandler instance = null;
	List<NewUserEvent> allListener = new ArrayList<NewUserEvent>();

	public static NewUserHandler getInstance() {
		if(instance == null) {
			instance = new NewUserHandler();
		}
		return instance;
	}
	
	public void addListener(NewUserEvent evt) {
		this.allListener.add(evt);
	}
	

	protected void notifyAllListener(Contact c) {
		AppSystem.getInstance().addContact(c);
		for (int i = 0; i < this.allListener.size(); i++) {
			notify(allListener.get(i), c);
		}
	}

	private void notify(NewUserEvent toNotify, final Contact c) {
		
		toNotify.reactOnNewUser(c);
		
//		if(toNotify instanceof ContactActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
//			final ContactActivity ca = (ContactActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewUser(c);
//				}
//			});
//		} else if (toNotify instanceof ChatActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
//			final ChatActivity ca = (ChatActivity)toNotify;
//			ca.findViewById(R.id.listView1).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewUser(c);
//				}
//			});
//		} else if (toNotify instanceof NewChatActivity && AppSystem.getInstance().getUserID() != c.getUserID()) {
//			final NewChatActivity ca = (NewChatActivity)toNotify;
//			ca.findViewById(R.id.list_contacts).post(new Runnable() {
//				public void run() {
//					Vibrator v = (Vibrator) ca.getSystemService(Context.VIBRATOR_SERVICE);
//					v.vibrate(500);
//					ca.reactOnNewUser(c);
//				}
//			});
//		} 
	}
}
