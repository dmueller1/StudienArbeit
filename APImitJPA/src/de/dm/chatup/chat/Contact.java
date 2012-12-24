package de.dm.chatup.chat;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

@Entity
public class Contact {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	int userid;
	String vorname;
	String nachname;
	
	
	public Contact() {
		
	}
	
	public Contact(int id, String vName, String nName) {
		this.userid = id;
		this.vorname = vName;
		this.nachname = nName;
	}
	
	public Contact(String vName, String nName) {
		this.vorname = vName;
		this.nachname = nName;
	}

	public int getUserID() {
		return userid;
	}

	public String getVorname() {
		return vorname;
	}

	public String getNachname() {
		return nachname;
	}
	
	public de.dm.chatup.network.Network.Contact toNetworkContact() {
		de.dm.chatup.network.Network.Contact contact = new de.dm.chatup.network.Network.Contact();
		contact.userid = userid;
		contact.nachname = nachname;
		contact.vorname = vorname;
		return contact;
	}

	
	
}
