package com.dm.chatup.activities;

import java.util.List;
import com.dm.chatup.system.ChatArrayAdapter;
import com.dm.chatup.system.NotificationMaker;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;
import de.dm.chatup.network.Network.Message;
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
import android.telephony.TelephonyManager;
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
		NotificationMaker.actualClass = this.getClass();
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
	
	public void showSettings(View v) {
		TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		Toast.makeText(getApplicationContext(), "Deine DeviceID: "+tManager.getDeviceId(), Toast.LENGTH_LONG).show();
	}


	public void reactOnNewUserInChat(Contact c, int chatID) {
		if(this.getClass() == NotificationMaker.actualClass) {
			Notification noti = NotificationMaker.makeNotification(
					c.getVorname() + " " + c.getNachname() + " ist Chat \""
							+ cuc.getChatFromID(chatID).getName()
							+ "\" beigetreten!", this, ChatActivity.class, chatID);
			notiMan.notify(ID_NOTIFIER_CONTACTS_NEW_USER_IN_CHAT, noti);
		}
	}

	public void reactOnNewChat(final Chat c) {
		if(this.getClass() == NotificationMaker.actualClass) {
			
			findViewById(R.id.listView1).post(new Runnable() {

				public void run() {
					List<Chat> meineChats = cuc.getAllMyChats();
					ListView chatsList = (ListView) findViewById(R.id.listView1);
					chatsList.setAdapter(new ChatArrayAdapter(getApplicationContext(), R.layout.listitem_chat,
							cuc.getMyUserID(), meineChats));
					Toast.makeText(getApplicationContext(), "Du wurdest zum Chat \"" + c.getName() + "\" hinzugefügt!", Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	public void reactOnNewUser(Contact c) {
		if(this.getClass() == NotificationMaker.actualClass) {
			Notification noti = NotificationMaker.makeNotification("Neuer Benutzer: " + c.getVorname() + " " + c.getNachname(), this, ContactActivity.class);
			notiMan.notify(ID_NOTIFIER_CONTACTS_NEW_USER, noti);
			Intent i = new Intent (this, ContactActivity.class);
			startActivity(i);
		}
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
		//cuc.writeMessagesAndLastUpdateFile(Environment.getExternalStorageDirectory() + "/lastUpdate.cuf", Environment.getExternalStorageDirectory() + "/messages.cuf");
		this.moveTaskToBack(true);
	}

	public void reactOnNewMessage(Chat chat, Message msg) {
		if(this.getClass() == NotificationMaker.actualClass) {
			if (cuc.getMyUserID() != msg.getErstellerID()) {
				Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);
				Notification noti = NotificationMaker.makeNotification(
						"Neue Nachricht in Chat \""
								+ chat.getName()
								+ "\"!", this, ChatActivity.class, chat.getChatID());
				notiMan.notify(ID_NOTIFIER_CONTACTS_NEW_MESSAGE, noti);
			}
		}
	}
	

}
