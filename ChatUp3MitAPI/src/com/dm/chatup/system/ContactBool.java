package com.dm.chatup.system;

import de.dm.chatup.network.Network.Contact;

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
