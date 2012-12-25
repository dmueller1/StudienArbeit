package com.dm.chatup.activities;

import java.util.List;
import com.dm.chatup.system.ChatHistoryAdapter;
import com.dm.chatup.system.NotificationMaker;

import de.dm.chatup.network.Network.Chat;
import de.dm.chatup.network.Network.Contact;
import de.dm.chatup.network.Network.Message;
import de.dm.chatup.client.ChatUpClient;
import de.dm.chatup.client.ClientMessageSendErrorException;
import de.dm.chatup.client.NewChatEvent;
import de.dm.chatup.client.NewChatHandler;
import de.dm.chatup.client.NewMessageEvent;
import de.dm.chatup.client.NewMessageHandler;
import de.dm.chatup.client.NewUserEvent;
import de.dm.chatup.client.NewUserHandler;
import de.dm.chatup.client.NewUserInChatEvent;
import de.dm.chatup.client.NewUserInChatHandler;
import de.dm.chatup.network.Network;
import de.dm.chatup.network.Network.SendNewMessage;

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
	
	ChatUpClient cuc;
	NotificationManager notiMan;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        NotificationMaker.actualClass = this.getClass();
        cuc = ChatUpClient.getInstance("dmmueller1.dyndns-web.com", 54555);
        notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NewMessageHandler.getInstance().addListener(this);
		NewUserInChatHandler.getInstance().addListener(this);
		NewChatHandler.getInstance().addListener(this);
		NewUserHandler.getInstance().addListener(this);
		
		cuc.setActualChatID(this.getIntent().getExtras().getInt("chatID"));
		((TextView)findViewById(R.id.actionbarTxt)).setText(cuc.getChatFromID(cuc.getActualChatID()).getName());

		// Fill ListView with messages: *****************************
		ListView messageHistory = (ListView) findViewById(R.id.listView1);

		List<Message> nachrichtenListe = cuc.getChatFromID(cuc.getActualChatID()).getMessages();

		if (nachrichtenListe != null) {
			
			messageHistory.setAdapter(new ChatHistoryAdapter(this, R.layout.listitem_history, cuc.getMyUserID(), nachrichtenListe));

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
		messageHistory.requestFocus();

    }
    
    
    
    public void sendMessage(View v) {

    	((ProgressBar)findViewById(R.id.progress_new_message)).setVisibility(View.VISIBLE);
		String nachricht = ((EditText) findViewById(R.id.editText1)).getText()
				.toString();
		
		Network.SendNewMessage snm = new Network.SendNewMessage();
		snm.erstellerID = cuc.getMyUserID();
		snm.chatID = cuc.getActualChatID();
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
    
    public void reactOnNewMessage(final Chat chat, final Message msg) {
    	
    	if(this.getClass() == NotificationMaker.actualClass) {
    	
			findViewById(R.id.listView1).post(new Runnable() {
				public void run() {
					ListView messageHistory = (ListView) findViewById(R.id.listView1);
					if(chat.getChatID() == cuc.getActualChatID()) {
						List<Message> nachrichtenListe = cuc.getChatFromID(cuc.getActualChatID()).getMessages();
						messageHistory.setAdapter(new ChatHistoryAdapter(getApplicationContext(), R.layout.listitem_history, cuc.getMyUserID(), nachrichtenListe));
						messageHistory.setSelection(messageHistory.getAdapter().getCount()-1);
					} else {
						if(cuc.getMyUserID() != msg.getErstellerID()) {
							Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
							v.vibrate(500);
							Notification noti = NotificationMaker.makeNotification("Neue Nachricht in Chat \"" + chat.getName() + "\"!", (Activity)messageHistory.getContext(), ChatActivity.class, chat.getChatID());
							notiMan.notify(ID_NOTIFIER_CHAT_NEW_MESSAGE, noti);
						}
					}
				}
			});
    	}
    	
    }

	public void reactOnNewChat(Chat c) {	
		if(this.getClass() == NotificationMaker.actualClass) {
			Notification noti = NotificationMaker.makeNotification("Du wurdest zum Chat \"" + c.getName() + "\" hinzugefügt!", this, ChatActivity.class, c.getChatID());
			notiMan.notify(ID_NOTIFIER_CHAT_NEW_CHAT, noti);
		}
	}

	public void reactOnNewUserInChat(Contact c, int chatID) {
		if(this.getClass() == NotificationMaker.actualClass) {
			if(cuc.getActualChatID() == chatID) {
				findViewById(R.id.listView1).post(new Runnable() {

					public void run() {
						ListView messageHistory = (ListView) findViewById(R.id.listView1);
						List<Message> nachrichtenListe = cuc.getChatFromID(cuc.getActualChatID()).getMessages();
						if (nachrichtenListe != null) {
							messageHistory.setAdapter(new ChatHistoryAdapter(getApplicationContext(), R.layout.listitem_history, cuc.getMyUserID(), nachrichtenListe));
							messageHistory.setSelection(messageHistory.getAdapter().getCount()-1);
						}
					}
				});
				
			} else {
				Notification noti = NotificationMaker.makeNotification(c.getVorname() + " " + c.getNachname() + " ist Chat \"" + cuc.getChatFromID(chatID).getName() + "\" beigetreten!", this, ChatActivity.class, chatID);
				notiMan.notify(ID_NOTIFIER_CHAT_NEW_USER_IN_CHAT, noti);
			}
		}
		
	}

	public void reactOnNewUser(Contact c) {
		if(this.getClass() == NotificationMaker.actualClass) {
			Notification noti = NotificationMaker.makeNotification("Neuer Benutzer: " + c.getVorname() + " " + c.getNachname(), this, ContactActivity.class);
			notiMan.notify(ID_NOTIFIER_CHAT_NEW_USER, noti);
			Intent i = new Intent (this, ChatActivity.class);
			i.putExtra("chatID", cuc.getActualChatID());
			startActivity(i);
		}
	}
	
	public void addUsers(View v) {
		Intent i = new Intent (this, ChatAddUserActivity.class);
		startActivity(i);
	}
	
	public void showSettings(View v) {
		TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		Toast.makeText(getApplicationContext(), "Deine DeviceID: "+tManager.getDeviceId(), Toast.LENGTH_LONG).show();
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
			
			try {
				cuc.sendNewMessage(messages[0].nachricht);
				publishProgress(new Boolean[] {true});
			} catch (ClientMessageSendErrorException e) {
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
