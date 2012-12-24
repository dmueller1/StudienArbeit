package com.dm.chatup.activities;

import java.util.ArrayList;
import java.util.List;
import com.dm.chatup.system.ContactBool;
import com.dm.chatup.system.NotificationMaker;
import com.dm.chatup.system.UserArrayAdapter;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;
import de.dm.chatup.network.Network.Message;
import de.dm.chatup.client.ChatUpClient;
import de.dm.chatup.client.ClientAddUserToChatErrorException;
import de.dm.chatup.client.NewChatEvent;
import de.dm.chatup.client.NewChatHandler;
import de.dm.chatup.client.NewMessageEvent;
import de.dm.chatup.client.NewMessageHandler;
import de.dm.chatup.client.NewUserEvent;
import de.dm.chatup.client.NewUserHandler;
import de.dm.chatup.client.NewUserInChatEvent;
import de.dm.chatup.client.NewUserInChatHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatAddUserActivity extends Activity implements NewChatEvent, NewMessageEvent, NewUserEvent, NewUserInChatEvent {
	
	ChatUpClient cuc;
	List<Contact> contactsToAdd = new ArrayList<Contact>();
	NotificationManager notiMan;
	
	private final int ID_NOTIFIER_NEW_CHAT_NEW_USER = 11;
	private final int ID_NOTIFIER_NEW_CHAT_NEW_CHAT = 12;
	private final int ID_NOTIFIER_NEW_CHAT_NEW_MESSAGE = 13;
	private final int ID_NOTIFIER_NEW_CHAT_NEW_USER_IN_CHAT = 14;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add_user);
        NotificationMaker.actualClass = this.getClass();
        cuc = ChatUpClient.getInstance("dmmueller1.dyndns-web.com", 54555);
        notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NewMessageHandler.getInstance().addListener(this);
		NewUserInChatHandler.getInstance().addListener(this);
		NewChatHandler.getInstance().addListener(this);
		NewUserHandler.getInstance().addListener(this);
        List<Contact> allContacts = cuc.getAllContactsInSystem();
        Chat aktChat = cuc.getChatFromID(cuc.getActualChatID());
        List<Contact> chatContacts = aktChat.getUsers();
        
        // Filtere alle schon im Chat beteiligten User aus ****************
        boolean schonDrin = false;
        List<Contact> filteredContacts = new ArrayList<Contact>();
        
        for (int alle = 0; alle < allContacts.size(); alle++) {
        	schonDrin = false;
        	for(int akt = 0; akt < chatContacts.size(); akt++) {
        		if(allContacts.get(alle).getUserID() == chatContacts.get(akt).getUserID()) {
        			schonDrin = true;
        		}
        	}
        	if(schonDrin == false) {
        		// User zu geeigneten Usern hinzufügen
        		filteredContacts.add(allContacts.get(alle));
        	}
        }
        
        ListView contactList = (ListView)findViewById(R.id.listAddUser);
		contactList.setAdapter(new UserArrayAdapter(this,
				R.layout.listitem_contact, cuc.getMyUserID(), filteredContacts));
		contactList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> listview, View arg1,
					int pos, long arg3) {
				ContactBool cb = (ContactBool) listview.getAdapter().getItem(
						pos);
				LinearLayout child1 = (LinearLayout) arg1;
				LinearLayout child2 = (LinearLayout) child1.getChildAt(0);
				CheckBox searchedCheckBox = (CheckBox) child2.getChildAt(0);
				searchedCheckBox.setChecked(!searchedCheckBox.isChecked());
				if (searchedCheckBox.isChecked() == true) {
					contactsToAdd.add(cb.getContact());
				} else {
					contactsToAdd.remove(cb.getContact());
				}
			}
		});
    }
    
    public void addUsers(View v) {
		((ProgressBar)findViewById(R.id.progress_chat_add_user)).setVisibility(View.VISIBLE);
		
			if (contactsToAdd.size() > 0) {
				
				int[] userIDs = new int[contactsToAdd.size()];
				
				for (int i = 0; i < contactsToAdd.size(); i++) {
					userIDs[i] = contactsToAdd.get(i).getUserID();
				}
				
				ATAddUsersToChat ac = new ATAddUsersToChat();
				ac.execute(userIDs);
			}
	}
    
    public void showSettings(View v) {
		TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		Toast.makeText(getApplicationContext(), "Deine DeviceID: "+tManager.getDeviceId(), Toast.LENGTH_LONG).show();
	}
    
    private class ATAddUsersToChat extends AsyncTask<int[], Boolean, Void> {

    	int chatID = cuc.getActualChatID();
    	
    	@Override
		protected Void doInBackground(int[]... params) {
			int[] userIDsToAdd = params[0];
			
			for(int i = 0; i < userIDsToAdd.length; i++) {
				try {
					cuc.addUserToChat(chatID, userIDsToAdd[i]);
				} catch (ClientAddUserToChatErrorException e) {
					this.cancel(true);
				}
			}
			return null;
		}
    	
		@Override
		protected void onCancelled() {
			((ProgressBar)findViewById(R.id.progress_chat_add_user)).setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "Hinzufügen fehlgeschlagen!", Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			((ProgressBar)findViewById(R.id.progress_chat_add_user)).setVisibility(View.VISIBLE);
			Toast.makeText(getApplicationContext(), "User erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getApplicationContext(), ChatActivity.class);
			i.putExtra("chatID", chatID);
			startActivity(i);
		}
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("chatID", cuc.getActualChatID());
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void reactOnNewChat(Chat c) {
		if(this.getClass() == NotificationMaker.actualClass) {
			Notification noti = NotificationMaker.makeNotification("Du wurdest zum Chat \"" + c.getName() + "\" hinzugefügt!", this, ChatActivity.class, c.getChatID());
			notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_CHAT, noti);
		}
	}

	public void reactOnNewUserInChat(Contact c, int chatID) {
		if(this.getClass() == NotificationMaker.actualClass) {
			Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
			Notification noti = NotificationMaker.makeNotification(
					c.getVorname() + " " + c.getNachname() + " ist Chat \""
							+ cuc.getChatFromID(chatID).getName()
							+ "\" beigetreten!", this, ChatActivity.class, chatID);
			notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_USER_IN_CHAT, noti);
		}
	}


	public void reactOnNewUser(Contact c) {
		if(this.getClass() == NotificationMaker.actualClass) {
			Notification noti = NotificationMaker.makeNotification("Neuer Benutzer: " + c.getVorname() + " " + c.getNachname(), this, ContactActivity.class);
			notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_USER, noti);
			Intent i = new Intent (this, NewChatActivity.class);
			startActivity(i);
		}
	}
	
	public void reactOnNewMessage(Chat chat, Message m) {
		if(this.getClass() == NotificationMaker.actualClass) {
			if (cuc.getMyUserID() != m.getErstellerID()) {
				Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);
				Notification noti = NotificationMaker.makeNotification(
						"Neue Nachricht in Chat \""
								+ chat.getName()
								+ "\"!", this, ChatActivity.class, chat.getChatID());
				notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_MESSAGE, noti);
			}
		}
	}

}
