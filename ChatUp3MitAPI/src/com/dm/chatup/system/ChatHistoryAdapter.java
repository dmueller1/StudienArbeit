package com.dm.chatup.system;

import java.util.ArrayList;
import java.util.List;

import com.dm.chatup.activities.R;

import de.dm.chatup.chat.Message;
import de.dm.chatup.client.ChatUpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatHistoryAdapter extends ArrayAdapter<Message> {
	
	private TextView txtMessage;
	private TextView txtInfo;
	private List<Message> messages = new ArrayList<Message>();
	private LinearLayout wrapper;
	private int userID;
	
	ChatUpClient cuc = ChatUpClient.getInstance("dmmueller1.dyndns-web.com", 54555);

	@Override
	public void add(Message msg) {
		messages.add(msg);
		super.add(msg);
	}

	public ChatHistoryAdapter(Context context, int textViewResourceId, int userID) {
		super(context, textViewResourceId);
		this.userID = userID;
	}
	
	public ChatHistoryAdapter(Context context, int textViewResourceId, int userID, List<Message> msgs) {
		super(context, textViewResourceId);
		this.userID = userID;
		this.messages = msgs;
	}

	public int getCount() {
		return this.messages.size();
	}

	public Message getItem(int index) {
		return this.messages.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_history, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.history_wrapper);

		Message msg = getItem(position);
		
		txtInfo = (TextView) row.findViewById(R.id.history_info);
		txtMessage = (TextView) row.findViewById(R.id.history_msg);
		
		String msgInfo = cuc.getUserFromID(msg.getErstellerID()).getVorname() + " " + 
							cuc.getUserFromID(msg.getErstellerID()).getNachname() + "\n" + 
							msg.getErstellDatum();

		txtInfo.setText(msgInfo);
		txtMessage.setText(msg.getNachricht());
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		if(msg.getErstellerID() == userID) {
			wrapper.setBackgroundResource(R.drawable.bubble_me);
	        params.gravity=Gravity.RIGHT;
	        wrapper.setLayoutParams(params);
		} else {
			wrapper.setBackgroundResource(R.drawable.bubble_other);
	        params.gravity=Gravity.LEFT;
	        wrapper.setLayoutParams(params);
		}
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}
