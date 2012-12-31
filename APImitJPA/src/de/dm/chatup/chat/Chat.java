package de.dm.chatup.chat;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
/**
 * Entitätsklasse, die den Chat repräsentiert
 * @author Daniel Müller
 *
 */
@Entity
public class Chat  {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	int chatid;
	
	String name;
	
	@ManyToMany(fetch = FetchType.EAGER)
	List<Contact> users = new ArrayList<Contact>();
	
	@OneToMany(fetch=FetchType.EAGER)
	List<Message> messages = new ArrayList<Message>();
	
	protected Chat() {
		
	}
	
	public Chat(int id, String name) {
		this.chatid = id;
		this.name = name;
		this.users = new ArrayList<Contact>();
		this.messages = new ArrayList<Message>();
	}
	
	public Chat(String name) {
		this.name = name;
		this.users = new ArrayList<Contact>();
		this.messages = new ArrayList<Message>();
	}
	
	public Chat(String name, List<Contact> users) {
		this.name = name;
		this.users = users;
		this.messages = new ArrayList<Message>();
	}
	
	public Chat(String name, List<Contact> users, List<Message> msgs) {
		this.name = name;
		this.users = users;
		this.messages = msgs;
	}

	public int getChatID() {
		return this.chatid;
	}

	public String getName() {
		return this.name;
	}
	
	public List<Contact> getUsers() {
		return this.users;
	}
	
	public void addUser(Contact user) {
		this.users.add(user);
	}
	
	public List<Message> getMessages() {
		return this.messages;
	}
	
	public void addMessage(Message msg) {
		this.messages.add(msg);
	}
	
	/**
	 * Wandelt die Entitätsklassen-Instanz in eine Paketklasse, die per Kryonet verschickt werden kann, um.
	 * @return Die umgewandelte Paketklasse
	 */
	public de.dm.chatup.network.Network.Chat toNetworkChat() {
		de.dm.chatup.network.Network.Chat chat = new de.dm.chatup.network.Network.Chat();
		chat.chatid = chatid;
		chat.name = name;
		
		for(int i = 0; i<users.size(); i++) {
			chat.users.add(users.get(i).toNetworkContact());
		}
		for(int i = 0; i<messages.size(); i++) {
			de.dm.chatup.network.Network.Message m = new de.dm.chatup.network.Network.Message();
			m.erstellDatum = messages.get(i).getErstellDatum();
			m.ersteller = messages.get(i).getErsteller().toNetworkContact();
			m.messageid = messages.get(i).getMessageID();
			m.nachricht = messages.get(i).getNachricht();
			chat.messages.add(m);
		}
		
		return chat;
	}

	
}
