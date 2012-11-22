package de.dm.chatup.chat;

public class Contact {
	
	int userID;
	String vorname;
	String nachname;
	
	public Contact(int id, String vName, String nName) {
		this.userID = id;
		this.vorname = vName;
		this.nachname = nName;
	}

	public int getUserID() {
		return userID;
	}

	public String getVorname() {
		return vorname;
	}

	public String getNachname() {
		return nachname;
	}
	
}
