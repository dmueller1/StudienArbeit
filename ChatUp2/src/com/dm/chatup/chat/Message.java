package com.dm.chatup.chat;

import java.sql.Date;

public class Message {
	
	int chatID;
	int erstellerID;
	String erstellDatum;
	String nachricht;
	
	public Message(int chatID, int ersID, String erstellDat, String nachricht) {
		this.chatID = chatID;
		this.erstellerID = ersID;
		this.erstellDatum = erstellDat;
		this.nachricht = nachricht;
	}

	public int getChatID() {
		return chatID;
	}

	public int getErstellerID() {
		return erstellerID;
	}

	public String getErstellDatum() {
		return erstellDatum;
	}

	public String getNachricht() {
		return nachricht;
	}
	
}
