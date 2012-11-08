package com.dm.chatup.system;

import java.util.ArrayList;
import java.util.List;

import com.dm.chatup.activities.R;
import com.dm.chatup.chat.Chat;
import com.dm.chatup.chat.Contact;
import com.dm.chatup.chat.Message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserArrayAdapter extends ArrayAdapter<ContactBool> {
	
	private CheckBox checkUser;
	private List<ContactBool> contacts = new ArrayList<ContactBool>();
	private int userID;

	@Override
	public void add(ContactBool cb) {
		
		if(cb.getContact().getUserID() != userID) {
			contacts.add(new ContactBool(cb.getContact(), cb.isChecked()));
		}
		super.add(new ContactBool(cb.getContact(), cb.isChecked()));
	}

	public UserArrayAdapter(Context context, int textViewResourceId, int userID) {
		super(context, textViewResourceId);
		this.userID = userID;
	}
	
	public UserArrayAdapter(Context context, int textViewResourceId, int userID, List<Contact> contacts) {
		super(context, textViewResourceId);
		this.userID = userID;
		
		for (int i = 0; i < contacts.size(); i++) {
			if(contacts.get(i).getUserID() != userID) {
				this.contacts.add(new ContactBool(contacts.get(i), false));
			}
		}
	}

	public int getCount() {
		return this.contacts.size();
	}

	public ContactBool getItem(int index) {
		return this.contacts.get(index);
	}
	

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_contact, parent, false);
		}

		Contact contact = getItem(position).getContact();
		
		checkUser = (CheckBox) row.findViewById(R.id.contact_check);
		checkUser.setText(contact.getVorname() + " " + contact.getNachname());
		
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}
