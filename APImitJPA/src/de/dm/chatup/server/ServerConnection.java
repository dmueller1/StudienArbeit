package de.dm.chatup.server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
import de.dm.chatup.chat.Device;
import de.dm.chatup.chat.Message;

public class ServerConnection {

	final static String jpaPersistenceUnit = "MyNewAPIWithJPA2";
	
	EntityManager entityManager;
	static ServerConnection instance;
	

	protected static ServerConnection getInstance() {
		if (instance == null) {
			instance = new ServerConnection();
		}
		return instance;
	}
	
	protected ServerConnection() {
		entityManager = Persistence.createEntityManagerFactory(jpaPersistenceUnit).createEntityManager();
	}
	
	protected int isUserRegistered(String deviceID) {
		try {
			Contact c = (Contact)entityManager.createQuery("SELECT d.besitzer from Device d WHERE d.deviceID = :deviceid").setParameter("deviceid", deviceID).getSingleResult();
			
			return c.getUserID();
		} catch(NoResultException nre) {
			return -1;
		}
	}
	
	protected de.dm.chatup.network.Network.Contact addNewUser(String deviceID, String vorname, String nachname) throws ServerActionErrorException {

		try {
			entityManager.getTransaction().begin();
			Contact c = new Contact(vorname, nachname);
			Device d = new Device(deviceID, c);
			entityManager.persist(c);
			entityManager.persist(d);
			entityManager.getTransaction().commit();
			entityManager.getEntityManagerFactory().getCache().evictAll();
			return c.toNetworkContact();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new ServerActionErrorException("Fehler beim Anlegen des neuen Benutzers!");
		}
	}

	protected de.dm.chatup.network.Network.Message sendNewMessage(int chatID, int userID, String erstellDatum,
			String nachricht) throws ServerActionErrorException {
		
		Chat chat = (Chat)entityManager.createQuery("SELECT c from Chat c where c.chatid = ?1").setParameter(1, chatID).getSingleResult();
		Contact ersteller = (Contact)entityManager.createQuery("SELECT c from Contact c where c.userid = ?1").setParameter(1, userID).getSingleResult();
		
		try {
			entityManager.getTransaction().begin();
			Message m = new Message(chat, ersteller, erstellDatum, nachricht);
			entityManager.persist(m);
			entityManager.getTransaction().commit();
			entityManager.getEntityManagerFactory().getCache().evictAll();
			return m.toNetworkMessage();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new ServerActionErrorException("Fehler beim Senden der neuen Nachricht!");
		}
		
	}

	protected List<de.dm.chatup.network.Network.Contact> getFriends() throws ServerActionErrorException {
		List<Contact> friends = entityManager.createQuery("SELECT c from Contact c").getResultList();
		List<de.dm.chatup.network.Network.Contact> returnContacts = new ArrayList<de.dm.chatup.network.Network.Contact>();
		
		for(int i = 0; i < friends.size(); i++) {
			returnContacts.add(friends.get(i).toNetworkContact());
		}
		return returnContacts;

	}

//	protected List<de.dm.chatup.network.Network.Message> getAllMessagesFromChat(int chatID, String lastUpdate) throws ServerActionErrorException {
//		List<Message> messages = entityManager.createQuery("SELECT m from Message m WHERE m.chat.chatid = ?1 AND m.erstellDatum > ?2").setParameter(1, chatID).setParameter(2, lastUpdate).getResultList();
//		List<de.dm.chatup.network.Network.Message> returnMsgs = new ArrayList<de.dm.chatup.network.Network.Message>();
//		
//		Chat c = (Chat)entityManager.createQuery("SELECT c from Chat c WHERE c.chatid = ?1").setParameter(1, chatID).getSingleResult();
//		de.dm.chatup.network.Network.Chat nc = c.toNetworkChat();
//		
//		for(int i = 0; i < messages.size(); i++) {
//			returnMsgs.add(messages.get(i).toNetworkMessage(nc));
//		}
//		return returnMsgs;
//	}

	protected List<de.dm.chatup.network.Network.Contact> getUsersFromChat(int chatID) throws ServerActionErrorException {
		List<Contact> users = entityManager.createQuery("SELECT c.users from Chat c WHERE c.chatid = ?1").setParameter(1, chatID).getResultList();
		List<de.dm.chatup.network.Network.Contact> returnUsers = new ArrayList<de.dm.chatup.network.Network.Contact>();
		
		for(int i = 0; i < users.size(); i++) {
			returnUsers.add(users.get(i).toNetworkContact());
		}
		return returnUsers;
	}

	protected List<de.dm.chatup.network.Network.Chat> getChatsFromUserID(int userID) throws ServerActionErrorException {
		List<Chat> chats = entityManager.createQuery("SELECT c from Chat c, Contact con WHERE con.userid = ?1 AND con MEMBER OF c.users").setParameter(1, userID).getResultList();
		List<de.dm.chatup.network.Network.Chat> returnChats = new ArrayList<de.dm.chatup.network.Network.Chat>();
		
		for(int i = 0; i < chats.size(); i++) {
			returnChats.add(chats.get(i).toNetworkChat());
		}
		return returnChats;
	}
	
	
	protected de.dm.chatup.network.Network.Chat createNewChat(String name) throws ServerActionErrorException {
		
		try {
			entityManager.getTransaction().begin();
			Chat chat = new Chat(name);
			entityManager.persist(chat);
			entityManager.getTransaction().commit();
			entityManager.getEntityManagerFactory().getCache().evictAll();
			return chat.toNetworkChat();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw new ServerActionErrorException("Fehler beim Anlegen des neuen Chats!");
		}
	}
	
	protected de.dm.chatup.network.Network.Chat addUserToChat(int chatID, int userID) throws ServerActionErrorException {
		
		Chat chat = (Chat)entityManager.createQuery("SELECT c from Chat c where c.chatid = ?1").setParameter(1, chatID).getSingleResult();
		Contact user = (Contact)entityManager.createQuery("SELECT c from Contact c where c.userid = ?1").setParameter(1, userID).getSingleResult();
		
			try {
				entityManager.getTransaction().begin();
				chat.addUser(user);
				entityManager.persist(chat);
				entityManager.getTransaction().commit();
				entityManager.getEntityManagerFactory().getCache().evictAll();
				return chat.toNetworkChat();
			} catch (Exception e) {
				entityManager.getTransaction().rollback();
				throw new ServerActionErrorException("Fehler beim Anlegen des neuen Chats!");
			}
		
	}

	public int addDeviceToUser(String oldDeviceID, String newDeviceID) throws ServerActionErrorException {
		try {
			Contact c = (Contact)entityManager.createQuery("SELECT d.besitzer from Device d WHERE d.deviceID = ?1").setParameter(1, oldDeviceID).getSingleResult();
			try {
				entityManager.getTransaction().begin();
				Device device = new Device(newDeviceID, c);
				entityManager.persist(device);
				entityManager.getTransaction().commit();
				entityManager.getEntityManagerFactory().getCache().evictAll();
				return c.getUserID();
			} catch (Exception e) {
				entityManager.getTransaction().rollback();
				throw new ServerActionErrorException("Fehler beim Anlegen des neuen Chats!");
			}
			
		} catch(NoResultException nre) {
			return -1;
		}
	}

}
