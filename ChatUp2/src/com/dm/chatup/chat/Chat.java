package com.dm.chatup.chat;

import java.util.ArrayList;
import java.util.List;

public class Chat {
	
	int chatID;
	String name;
	List<Contact> users;
	List<Message> messages;
	
	public Chat(int id, String name) {
		this.chatID = id;
		this.name = name;
		this.users = new ArrayList<Contact>();
		this.messages = new ArrayList<Message>();
	}
	
	public Chat(int id, String name, List<Contact> users) {
		this.chatID = id;
		this.name = name;
		this.users = users;
		this.messages = new ArrayList<Message>();
	}
	
	public Chat(int id, String name, List<Contact> users, List<Message> msgs) {
		this.chatID = id;
		this.name = name;
		this.users = users;
		this.messages = msgs;
	}

	public int getChatID() {
		return this.chatID;
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
	
}
