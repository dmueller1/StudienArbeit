package de.dm.chatup.chat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * Entitätsklasse, die eine Nachricht repräsentiert
 * @author Daniel Müller
 *
 */
@Entity
public class Message {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	int messageid;
	@ManyToOne
	@JoinColumn(name="erstellerid")
	Contact ersteller;
	
	String erstellDatum;
	@Lob
	String nachricht;
	
	protected Message() {
		
	}

	public Message(Chat chat, Contact ersteller, String erstellDat, String nachricht) {
		this.ersteller = ersteller;
		this.erstellDatum = erstellDat;
		this.nachricht = nachricht;
		chat.messages.add(this);
	}

	public Contact getErsteller() {
		return ersteller;
	}

	public String getErstellDatum() {
		return erstellDatum;
	}

	public String getNachricht() {
		return nachricht;
	}
	
	public int getMessageID() {
		return this.messageid;
	}

	/**
	 * Wandelt die Entitätsklassen-Instanz in eine Paketklasse, die per Kryonet verschickt werden kann, um.
	 * @return Die umgewandelte Paketklasse
	 */
	public de.dm.chatup.network.Network.Message toNetworkMessage() {
		de.dm.chatup.network.Network.Message msg = new de.dm.chatup.network.Network.Message();
		msg.messageid = messageid;
		msg.erstellDatum = erstellDatum;
		msg.ersteller = ersteller.toNetworkContact();
		msg.nachricht = nachricht;
		return msg;
	}
	
}
