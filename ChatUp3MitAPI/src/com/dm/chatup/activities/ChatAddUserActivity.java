package com.dm.chatup.activities;

import java.util.ArrayList;
import java.util.List;
import com.dm.chatup.system.ContactBool;
import com.dm.chatup.system.UserArrayAdapter;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
import de.dm.chatup.client.ChatUpClient;
import de.dm.chatup.client.ClientAddUserToChatErrorException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

public class ChatAddUserActivity extends Activity {
	
	ChatUpClient cuc;
	List<Contact> contactsToAdd = new ArrayList<Contact>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add_user);
        cuc = ChatUpClient.getInstance("dmmueller1.dyndns-web.com", 54555);
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

}
