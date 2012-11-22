package com.dm.chatup.activities;

import java.util.List;
import com.dm.chatup.system.ChatArrayAdapter;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
import de.dm.chatup.chat.Message;
import de.dm.chatup.client.ChatUpClient;
import de.dm.chatup.client.NewChatEvent;
import de.dm.chatup.client.NewChatHandler;
import de.dm.chatup.client.NewMessageEvent;
import de.dm.chatup.client.NewMessageHandler;
import de.dm.chatup.client.NewUserEvent;
import de.dm.chatup.client.NewUserHandler;
import de.dm.chatup.client.NewUserInChatEvent;
import de.dm.chatup.client.NewUserInChatHandler;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ContactActivity extends Activity implements NewMessageEvent,
		NewUserInChatEvent, NewChatEvent, NewUserEvent {

	private final int ID_NOTIFIER_CONTACTS_NEW_USER = 4;
	private final int ID_NOTIFIER_CONTACTS_NEW_USER_IN_CHAT = 5;
	private final int ID_NOTIFIER_CONTACTS_NEW_MESSAGE = 6;
	
	ChatUpClient cuc;

	NotificationManager notiMan;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);
		cuc = ChatUpClient.getInstance("dmmueller1.dyndns-web.com", 54555);
		
		notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NewMessageHandler.getInstance().addListener(this);
		NewUserInChatHandler.getInstance().addListener(this);
		NewChatHandler.getInstance().addListener(this);
		NewUserHandler.getInstance().addListener(this);

		// Fill ListView with friends' names: *****************************
		List<Chat> meineChats = cuc.getAllMyChats();

		ListView chatsList = (ListView) findViewById(R.id.listView1);
		chatsList.setAdapter(new ChatArrayAdapter(this, R.layout.listitem_chat,
				cuc.getMyUserID(), meineChats));
		chatsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {

				Intent i = new Intent(ContactActivity.this, ChatActivity.class);
				i.putExtra("chatID", cuc.getAllMyChats().get(pos).getChatID());
				startActivity(i);
			}

		});

	}
	
	public void addChat(View v) {
		Intent i = new Intent(this, NewChatActivity.class);
		startActivity(i);
	}


	public void reactOnNewUserInChat(Contact c, int chatID) {

//		Notification noti = mySystem.makeNotification(
//				c.getVorname() + " " + c.getNachname() + " ist Chat \""
//						+ mySystem.getChatFromID(chatID).getName()
//						+ "\" beigetreten!", this, ChatActivity.class, chatID);
//		notiMan.notify(ID_NOTIFIER_CONTACTS_NEW_USER_IN_CHAT, noti);
	}

	public void reactOnNewMessage(Message msg) {

		if (cuc.getMyUserID() != msg.getErstellerID()) {
			Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
//			Notification noti = mySystem.makeNotification(
//					"Neue Nachricht in Chat \""
//							+ mySystem.getChatFromID(msg.getChatID()).getName()
//							+ "\"!", this, ChatActivity.class, msg.getChatID());
//			notiMan.notify(ID_NOTIFIER_CONTACTS_NEW_MESSAGE, noti);
		}

	}

	public void reactOnNewChat(Chat c) {
		List<Chat> meineChats = cuc.getAllMyChats();
		ListView chatsList = (ListView) findViewById(R.id.listView1);
		chatsList.setAdapter(new ChatArrayAdapter(this, R.layout.listitem_chat,
				cuc.getMyUserID(), meineChats));
		Toast.makeText(getApplicationContext(), "Du wurdest zum Chat \"" + c.getName() + "\" hinzugefügt!", Toast.LENGTH_LONG).show();
	}

	public void reactOnNewUser(Contact c) {
//		Notification noti = mySystem.makeNotification("Neuer Benutzer: " + c.getVorname() + " " + c.getNachname(), this, ContactActivity.class);
//		notiMan.notify(ID_NOTIFIER_CONTACTS_NEW_USER, noti);
		Intent i = new Intent (this, ContactActivity.class);
		startActivity(i);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	public void close() {
		cuc.writeMessagesAndLastUpdateFile(Environment.getExternalStorageDirectory() + "/lastUpdate.cuf", Environment.getExternalStorageDirectory() + "/messages.cuf");
		this.moveTaskToBack(true);
	}

}
