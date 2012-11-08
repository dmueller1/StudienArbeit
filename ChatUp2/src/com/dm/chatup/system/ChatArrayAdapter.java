package com.dm.chatup.system;

import java.util.ArrayList;
import java.util.List;

import com.dm.chatup.activities.R;
import com.dm.chatup.chat.Chat;
import com.dm.chatup.chat.Message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<Chat> {
	
	private TextView txtChat;
	private TextView txtInfo;
	private List<Chat> chats = new ArrayList<Chat>();
	private int userID;

	@Override
	public void add(Chat chat) {
		chats.add(chat);
		super.add(chat);
	}

	public ChatArrayAdapter(Context context, int textViewResourceId, int userID) {
		super(context, textViewResourceId);
		this.userID = userID;
	}
	
	public ChatArrayAdapter(Context context, int textViewResourceId, int userID, List<Chat> chats) {
		super(context, textViewResourceId);
		this.userID = userID;
		this.chats = chats;
	}

	public int getCount() {
		return this.chats.size();
	}

	public Chat getItem(int index) {
		return this.chats.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_chat, parent, false);
		}

		Chat chat = getItem(position);
		
		txtChat = (TextView) row.findViewById(R.id.chat_name);
		txtInfo = (TextView) row.findViewById(R.id.chat_info);
		
		String chatInfo = "";
		
		for(int i = 0 ; i < chat.getUsers().size(); i++) {
			
			if(chat.getUsers().get(i).getUserID() == userID) {
				chatInfo += "Ich; ";
			} else {
				chatInfo += chat.getUsers().get(i).getVorname() + " " + chat.getUsers().get(i).getNachname() + "; ";
			}
		}
		txtInfo.setText(chatInfo);
		txtChat.setText(chat.getName());
		
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}
