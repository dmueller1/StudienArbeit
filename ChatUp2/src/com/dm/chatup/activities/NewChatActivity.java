package com.dm.chatup.activities;

import java.util.ArrayList;
import java.util.List;
import com.dm.chatup.chat.Chat;
import com.dm.chatup.chat.Contact;
import com.dm.chatup.chat.Message;
import com.dm.chatup.events.*;
import com.dm.chatup.internet.Network;
import com.dm.chatup.system.AppSystem;
import com.dm.chatup.system.AppSystemOnline;
import com.dm.chatup.system.ContactBool;
import com.dm.chatup.system.UserArrayAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NewChatActivity extends Activity implements NewMessageEvent, NewUserInChatEvent, NewChatEvent, NewUserEvent {

	AppSystem mySystem;
	AppSystemOnline mySystemOnline;
	List<Contact> contactsToAdd = new ArrayList<Contact>();
	private final int ID_NOTIFIER_NEW_CHAT_NEW_USER = 7;
	private final int ID_NOTIFIER_NEW_CHAT_NEW_CHAT = 8;
	private final int ID_NOTIFIER_NEW_CHAT_NEW_MESSAGE = 9;
	private final int ID_NOTIFIER_NEW_CHAT_NEW_USER_IN_CHAT = 10;

	NotificationManager notiMan;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_chat);

		NewMessageHandler.getInstance().addListener(this);
		NewUserInChatHandler.getInstance().addListener(this);
		NewChatHandler.getInstance().addListener(this);
		NewUserHandler.getInstance().addListener(this);
		
		mySystem = AppSystem.getInstance();
		mySystemOnline = AppSystemOnline.getInstance();
		notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		ListView contactList = (ListView) findViewById(R.id.list_contacts);
		contactList.setAdapter(new UserArrayAdapter(this,
				R.layout.listitem_contact, mySystem.getUserID(), mySystem
						.getMyContacts()));
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

	public void createNewChat(View v) {
		((ProgressBar)findViewById(R.id.progress_new_chat)).setVisibility(View.VISIBLE);
		EditText chatName = (EditText) findViewById(R.id.txtChatName);
		if (chatName.getText().toString().length() > 0) {

			String[] userIDs = new String[contactsToAdd.size() + 1];

			if (contactsToAdd.size() > 0) {
				for (int i = 0; i < contactsToAdd.size(); i++) {
					userIDs[i] = String.valueOf(contactsToAdd.get(i)
							.getUserID());
				}
				userIDs[contactsToAdd.size()] = String.valueOf(mySystem
						.getUserID());

				Network.AddNewChat anc = new Network.AddNewChat();
				anc.name = chatName.getText().toString();
				anc.userIDs = userIDs;

				ATAddChat ac = new ATAddChat();
				ac.execute(anc);
			}
		}
	}

	public void reactOnNewChat(Chat c) {
		Notification noti = mySystem.makeNotification("Du wurdest zum Chat \"" + c.getName() + "\" hinzugefügt!", this, ChatActivity.class, c.getChatID());
		notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_CHAT, noti);
	}

	public void reactOnNewUserInChat(Contact c, int chatID) {
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(500);
		Notification noti = mySystem.makeNotification(
				c.getVorname() + " " + c.getNachname() + " ist Chat \""
						+ mySystem.getChatFromID(chatID).getName()
						+ "\" beigetreten!", this, ChatActivity.class, chatID);
		notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_USER_IN_CHAT, noti);
	}

	public void reactOnNewMessage(Message m) {
		if (mySystem.getUserID() != m.getErstellerID()) {
			Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
			Notification noti = mySystem.makeNotification(
					"Neue Nachricht in Chat \""
							+ mySystem.getChatFromID(m.getChatID()).getName()
							+ "\"!", this, ChatActivity.class, m.getChatID());
			notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_MESSAGE, noti);
		}
	}

	public void reactOnNewUser(Contact c) {
		Notification noti = mySystem.makeNotification("Neuer Benutzer: " + c.getVorname() + " " + c.getNachname(), this, ContactActivity.class);
		notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_USER, noti);
		Intent i = new Intent (this, NewChatActivity.class);
		startActivity(i);
	}
	
	private class ATAddChat extends AsyncTask<Network.AddNewChat, Boolean, Void> {

		@Override
		protected void onProgressUpdate(Boolean... values) {
			if(values[0] == false) {
				Toast.makeText(getApplicationContext(), "Fehler beim Erstellen des Chats", Toast.LENGTH_LONG).show();
			}
			((ProgressBar)findViewById(R.id.progress_new_chat)).setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(Network.AddNewChat... chats) {
			
			boolean success = mySystemOnline.createNewChat(chats[0]);
			publishProgress(new Boolean[] {success});
			
			if(success) {
				Intent i = new Intent(getApplicationContext(), ContactActivity.class);
				startActivity(i);
			} else {
				this.cancel(true);
			}
			
			return null;
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, ContactActivity.class);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
