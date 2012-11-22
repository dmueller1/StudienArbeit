package com.dm.chatup.activities;

import java.util.ArrayList;
import java.util.List;
import com.dm.chatup.system.ContactBool;
import com.dm.chatup.system.UserArrayAdapter;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
import de.dm.chatup.chat.Message;
import de.dm.chatup.client.ChatUpClient;
import de.dm.chatup.client.ClientCreateChatErrorException;
import de.dm.chatup.client.NewChatEvent;
import de.dm.chatup.client.NewChatHandler;
import de.dm.chatup.client.NewMessageEvent;
import de.dm.chatup.client.NewMessageHandler;
import de.dm.chatup.client.NewUserEvent;
import de.dm.chatup.client.NewUserHandler;
import de.dm.chatup.client.NewUserInChatEvent;
import de.dm.chatup.client.NewUserInChatHandler;
import de.dm.chatup.network.Network;

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

	ChatUpClient cuc;
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

		cuc = ChatUpClient.getInstance("dmmueller1.dyndns-web.com", 54555);
		NewMessageHandler.getInstance().addListener(this);
		NewUserInChatHandler.getInstance().addListener(this);
		NewChatHandler.getInstance().addListener(this);
		NewUserHandler.getInstance().addListener(this);
		
		
		notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		ListView contactList = (ListView) findViewById(R.id.list_contacts);
		contactList.setAdapter(new UserArrayAdapter(this,
				R.layout.listitem_contact, cuc.getMyUserID(), cuc.getAllContactsInSystem()));
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
				userIDs[contactsToAdd.size()] = String.valueOf(cuc.getMyUserID());

				Network.AddNewChat anc = new Network.AddNewChat();
				anc.name = chatName.getText().toString();
				anc.userIDs = userIDs;

				ATAddChat ac = new ATAddChat();
				ac.execute(anc);
			}
		}
	}

	public void reactOnNewChat(Chat c) {
//		Notification noti = mySystem.makeNotification("Du wurdest zum Chat \"" + c.getName() + "\" hinzugefügt!", this, ChatActivity.class, c.getChatID());
//		notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_CHAT, noti);
	}

	public void reactOnNewUserInChat(Contact c, int chatID) {
		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(500);
//		Notification noti = mySystem.makeNotification(
//				c.getVorname() + " " + c.getNachname() + " ist Chat \""
//						+ mySystem.getChatFromID(chatID).getName()
//						+ "\" beigetreten!", this, ChatActivity.class, chatID);
//		notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_USER_IN_CHAT, noti);
	}

	public void reactOnNewMessage(Message m) {
		if (cuc.getMyUserID() != m.getErstellerID()) {
			Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
//			Notification noti = mySystem.makeNotification(
//					"Neue Nachricht in Chat \""
//							+ mySystem.getChatFromID(m.getChatID()).getName()
//							+ "\"!", this, ChatActivity.class, m.getChatID());
//			notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_MESSAGE, noti);
		}
	}

	public void reactOnNewUser(Contact c) {
//		Notification noti = mySystem.makeNotification("Neuer Benutzer: " + c.getVorname() + " " + c.getNachname(), this, ContactActivity.class);
//		notiMan.notify(ID_NOTIFIER_NEW_CHAT_NEW_USER, noti);
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
			
			try {
				cuc.addNewChat(chats[0]);
				publishProgress(new Boolean[] {true});
				Intent i = new Intent(getApplicationContext(), ContactActivity.class);
				startActivity(i);
			} catch (ClientCreateChatErrorException e) {
				publishProgress(new Boolean[] {false});
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
