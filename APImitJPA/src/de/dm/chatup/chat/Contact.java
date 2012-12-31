package de.dm.chatup.chat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entitätsklasse, die einen Benutzer repräsentiert
 * @author Daniel Müller
 *
 */
@Entity
public class Contact {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	int userid;
	String vorname;
	String nachname;
	
	
	protected Contact() {
		
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
	
	/**
	 * Wandelt die Entitätsklassen-Instanz in eine Paketklasse, die per Kryonet verschickt werden kann, um.
	 * @return Die umgewandelte Paketklasse
	 */
	public de.dm.chatup.network.Network.Contact toNetworkContact() {
		de.dm.chatup.network.Network.Contact contact = new de.dm.chatup.network.Network.Contact();
		contact.userid = userid;
		contact.nachname = nachname;
		contact.vorname = vorname;
		return contact;
	}

	
	
}
