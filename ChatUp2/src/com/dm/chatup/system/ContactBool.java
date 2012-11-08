package com.dm.chatup.system;

import com.dm.chatup.chat.Contact;

public class ContactBool {
	
	Contact contact;
	boolean bool;
	
	ContactBool(Contact c, boolean b) {
		this.contact = c;
		this.bool = b;
	}

	public Contact getContact() {
		return contact;
	}

	public boolean isChecked() {
		return bool;
	}
}
