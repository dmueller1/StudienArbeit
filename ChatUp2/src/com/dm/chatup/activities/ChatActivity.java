package com.dm.chatup.activities;

import java.util.List;

import com.dm.chatup.chat.Chat;
import com.dm.chatup.chat.Contact;
import com.dm.chatup.chat.Message;
import com.dm.chatup.events.*;
import com.dm.chatup.internet.Network;
import com.dm.chatup.internet.Network.SendNewMessage;
import com.dm.chatup.system.AppSystem;
import com.dm.chatup.system.AppSystemOnline;
import com.dm.chatup.system.ChatHistoryAdapter;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatActivity extends Activity implements NewMessageEvent, NewUserInChatEvent, NewChatEvent, NewUserEvent {
	
	

	private final int ID_NOTIFIER_CHAT_NEW_USER = 0;
	private final int ID_NOTIFIER_CHAT_NEW_CHAT = 1;
	private final int ID_NOTIFIER_CHAT_NEW_MESSAGE = 2;
	private final int ID_NOTIFIER_CHAT_NEW_USER_IN_CHAT = 3;
	
	AppSystem mySystem;
	AppSystemOnline mySystemOnline;
	NotificationManager notiMan;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mySystem = AppSystem.getInstance();
        mySystemOnline = AppSystemOnline.getInstance();
        notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NewMessageHandler.getInstance().addListener(this);
		NewUserInChatHandler.getInstance().addListener(this);
		NewChatHandler.getInstance().addListener(this);
		NewUserHandler.getInstance().addListener(this);
		
		mySystem.setOpenChat(this.getIntent().getExtras().getInt("chatID"));

		// Fill ListView with messages: *****************************
		ListView messageHistory = (ListView) findViewById(R.id.listView1);

		List<Message> nachrichtenListe = mySystem.getChatFromID(mySystem.getOpenChat()).getMessages();

		if (nachrichtenListe != null) {
			
			messageHistory.setAdapter(new ChatHistoryAdapter(this, R.layout.listitem_history, mySystem.getUserID(), nachrichtenListe));

			messageHistory.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {

					Toast.makeText(getApplicationContext(), "Test beendet",
							Toast.LENGTH_LONG).show();
				}

			});
			
			messageHistory.setSelection(messageHistory.getAdapter().getCount()-1);
		}

		((EditText)findViewById(R.id.editText1)).setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					sendMessage(v);
					return true;
				}
				return false;
			}

	    });

    }
    
    public void sendMessage(View v) {

    	((ProgressBar)findViewById(R.id.progress_new_message)).setVisibility(View.VISIBLE);
		String nachricht = ((EditText) findViewById(R.id.editText1)).getText()
				.toString();
		
		Network.SendNewMessage snm = new Network.SendNewMessage();
		snm.erstellerID = mySystem.getUserID();
		snm.chatID = mySystem.getOpenChat();
		snm.nachricht = nachricht;
		
		ATSendNewMessage sm = new ATSendNewMessage();
		sm.execute(snm);
		
		((EditText) findViewById(R.id.editText1)).setText("");
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat, menu);
        return true;
    }

	public void reactOnNewMessage(Message msg) {
		
		if(msg.getChatID() == mySystem.getOpenChat()) {
			ListView messageHistory = (ListView) findViewById(R.id.listView1);
			List<Message> nachrichtenListe = mySystem.getChatFromID(mySystem.getOpenChat()).getMessages();
			messageHistory.setAdapter(new ChatHistoryAdapter(this, R.layout.listitem_history, mySystem.getUserID(), nachrichtenListe));
			messageHistory.setSelection(messageHistory.getAdapter().getCount()-1);
		} else {
			if(mySystem.getUserID() != msg.getErstellerID()) {
				Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);
				Notification noti = mySystem.makeNotification("Neue Nachricht in Chat \"" + mySystem.getChatFromID(msg.getChatID()).getName() + "\"!", this, ChatActivity.class, msg.getChatID());
				notiMan.notify(ID_NOTIFIER_CHAT_NEW_MESSAGE, noti);
			}
		}
		
	}

	public void reactOnNewChat(Chat c) {		
		Notification noti = mySystem.makeNotification("Du wurdest zum Chat \"" + c.getName() + "\" hinzugefügt!", this, ChatActivity.class, c.getChatID());
		notiMan.notify(ID_NOTIFIER_CHAT_NEW_CHAT, noti);
	}

	public void reactOnNewUserInChat(Contact c, int chatID) {
		
		if(mySystem.getOpenChat() == chatID) {
			ListView messageHistory = (ListView) findViewById(R.id.listView1);
			List<Message> nachrichtenListe = mySystem.getChatFromID(mySystem.getOpenChat()).getMessages();
			if (nachrichtenListe != null) {
				messageHistory.setAdapter(new ChatHistoryAdapter(this, R.layout.listitem_history, mySystem.getUserID(), nachrichtenListe));
				messageHistory.setSelection(messageHistory.getAdapter().getCount()-1);
			}
		} else {
			Notification noti = mySystem.makeNotification(c.getVorname() + " " + c.getNachname() + " ist Chat \"" + mySystem.getChatFromID(chatID).getName() + "\" beigetreten!", this, ChatActivity.class, chatID);
			notiMan.notify(ID_NOTIFIER_CHAT_NEW_USER_IN_CHAT, noti);
		}
	}

	public void reactOnNewUser(Contact c) {
		Notification noti = mySystem.makeNotification("Neuer Benutzer: " + c.getVorname() + " " + c.getNachname(), this, ContactActivity.class);
		notiMan.notify(ID_NOTIFIER_CHAT_NEW_USER, noti);
		Intent i = new Intent (this, ChatActivity.class);
		i.putExtra("chatID", mySystem.getOpenChat());
		startActivity(i);
	}
	
	public void addUsers(View v) {
		Intent i = new Intent (this, ChatAddUserActivity.class);
		startActivity(i);
	}
	
	private class ATSendNewMessage extends AsyncTask<Network.SendNewMessage, Boolean, Void> {

		@Override
		protected void onProgressUpdate(Boolean... values) {
			if(values[0] == false) {
				Toast.makeText(getApplicationContext(), "Fehler beim Senden der Nachricht!", Toast.LENGTH_LONG).show();
			}
			((ProgressBar)findViewById(R.id.progress_new_message)).setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(SendNewMessage... messages) {
			
			boolean success = mySystemOnline.sendNewMessage(messages[0].erstellerID, messages[0].chatID, messages[0].nachricht);
			publishProgress(new Boolean[] {success});
			
			if(!success) {
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
