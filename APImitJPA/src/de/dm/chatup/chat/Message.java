package de.dm.chatup.chat;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.dm.chatup.client.AppSystem;

@Entity
public class Message {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	int messageid;
	@ManyToOne
	@JoinColumn(name="erstellerid")
	Contact ersteller;
	
	String erstellDatum;
	String nachricht;
	
	
	public Message() {
		
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

	
	public de.dm.chatup.network.Network.Message toNetworkMessage() {
		de.dm.chatup.network.Network.Message msg = new de.dm.chatup.network.Network.Message();
		msg.messageid = messageid;
		msg.erstellDatum = erstellDatum;
		msg.ersteller = ersteller.toNetworkContact();
		msg.nachricht = nachricht;
		return msg;
	}


	public int getMessageID() {
		return this.messageid;
	}
	
}
