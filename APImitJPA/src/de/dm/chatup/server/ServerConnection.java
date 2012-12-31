package de.dm.chatup.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import de.dm.chatup.chat.Chat;
import de.dm.chatup.chat.Contact;
import de.dm.chatup.chat.Device;
import de.dm.chatup.chat.Message;

/**
 * Klasse, die die Verbindung zwischen Server und Datenbank repräsentiert und Daten in diese einträgt bzw, aus dieser ausliest
 * @author Daniel Müller
 *
 */
public class ServerConnection {

	final static String jpaPersistenceUnit = "MyNewAPIWithJPA2";
	
	EntityManager entityManager;
	static ServerConnection instance;
	

	protected static ServerConnection getInstance(String dbLink, String username, String password, String database) {
		if (instance == null) {
			instance = new ServerConnection(dbLink, username, password, database);
		}
		return instance;
	}
	
	private ServerConnection(String dbLink, String username, String password, String database) {
		
		Properties props = new Properties();
		props.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://" + dbLink + ":3306/" + database);
		props.setProperty("javax.persistence.jdbc.user", username);
		props.setProperty("javax.persistence.jdbc.password", password);
		entityManager = Persistence.createEntityManagerFactory(jpaPersistenceUnit, props).createEntityManager();
	}
	
	/**
	 * Prüft in der Datenbank, ob die übergebene Geräte-ID bereits auf einen Benutzer angemeldet ist
	 * @param deviceID Die zu überprüfende Gerätekennung
	 * @return ID des Benutzers, falls das Gerät bereits auf einen registriert ist
	 */
	protected int isUserRegistered(String deviceID) {
		try {
			Contact c = (Contact)entityManager.createQuery("SELECT d.besitzer from Device d WHERE d.deviceID = :deviceid").setParameter("deviceid", deviceID).getSingleResult();
			
			return c.getUserID();
		} catch(NoResultException nre) {
			return -1;
		}
	}
	
	/**
	 * Fügt einen neuen Benutzer in die Datenbank ein
	 * @param deviceID Gerätekennung des Geräts
	 * @param vorname Vorname des Benutzers
	 * @param nachname Nachname des Benutzers
	 * @return Paketklasse des neu angelegten Benutzers
	 * @throws ServerActionErrorException Fehler, der aufgerufen wird, wenn das Erstellen fehlschlägt
	 */
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

	/**
	 * Speichert eine neue Nachricht in der Datenbank
	 * @param chatID ID des Chats, in dem die Nachricht erstellt wurde
	 * @param userID ID des Benutzers, der die Nachricht gesendet hat
	 * @param erstellDatum Erstelldatum der Nachricht (Serverzeit bei erhalt der Paketklasse)
	 * @param nachricht Text der gesendeten Nachricht
	 * @return Paketklasse der gespeicherten Nachricht
	 * @throws ServerActionErrorException Fehler, der aufgerufen wird, wenn das Senden fehlschlägt
	 */
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
	
	/**
	 * Liest alle Chat-Benutzer aus der Datenbank aus
	 * @return Liste der Paketklassen, die die Benutzer repräsentieren
	 * @throws ServerActionErrorException Fehler, der ausgelöst wird, wenn das Auslesen fehlschlägt
	 */
	protected List<de.dm.chatup.network.Network.Contact> getFriends() throws ServerActionErrorException {
		@SuppressWarnings("unchecked")
		List<Contact> friends = entityManager.createQuery("SELECT c from Contact c").getResultList();
		List<de.dm.chatup.network.Network.Contact> returnContacts = new ArrayList<de.dm.chatup.network.Network.Contact>();
		
		for(int i = 0; i < friends.size(); i++) {
			returnContacts.add(friends.get(i).toNetworkContact());
		}
		return returnContacts;
	}

	/**
	 * Liest die im Chat vorhandenen Benutzer aus
	 * @param chatID ID des gesuchten Chats
	 * @return Liste der Paketklassen, die die Benutzer repräsentieren
	 * @throws ServerActionErrorException Fehler, der ausgelöst wird, wenn das Auslesen fehlschlägt
	 */
	protected List<de.dm.chatup.network.Network.Contact> getUsersFromChat(int chatID) throws ServerActionErrorException {
		@SuppressWarnings("unchecked")
		List<Contact> users = entityManager.createQuery("SELECT c.users from Chat c WHERE c.chatid = ?1").setParameter(1, chatID).getResultList();
		List<de.dm.chatup.network.Network.Contact> returnUsers = new ArrayList<de.dm.chatup.network.Network.Contact>();
		
		for(int i = 0; i < users.size(); i++) {
			returnUsers.add(users.get(i).toNetworkContact());
		}
		return returnUsers;
	}

	/**
	 * Liest die zum Benutzer gehörenden Chats aus (Chats in denen der Benutzer Mitglied ist)
	 * @param userID ID des Benutzers, dessen Chats ausgelesen werden sollen
	 * @return Liste der Paketklassen, die die Chats repräsentieren
	 * @throws ServerActionErrorException Fehler, der ausgelöst wird, wenn das Auslesen fehlschlägt
	 */
	protected List<de.dm.chatup.network.Network.Chat> getChatsFromUserID(int userID) throws ServerActionErrorException {
		@SuppressWarnings("unchecked")
		List<Chat> chats = entityManager.createQuery("SELECT c from Chat c, Contact con WHERE con.userid = ?1 AND con MEMBER OF c.users").setParameter(1, userID).getResultList();
		List<de.dm.chatup.network.Network.Chat> returnChats = new ArrayList<de.dm.chatup.network.Network.Chat>();
		
		for(int i = 0; i < chats.size(); i++) {
			returnChats.add(chats.get(i).toNetworkChat());
		}
		return returnChats;
	}
	
	/**
	 * Speichert einen neuen Chat in der Datenbank
	 * @param name Name des zu speichernden Chats
	 * @return Paketklasse des angelegten Chats
	 * @throws ServerActionErrorException Fehler, der ausgelöst wird, wenn das Speichern fehlschlägt
	 */
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
	
	/**
	 * Fügt einen Benutzer zu einem Chat hinzu
	 * @param chatID ID des Chats, in welchen der Benutzer hinzugefügt werden soll
	 * @param userID ID des hinzuzufügenden Benutzers
	 * @return Paketklasse des Chats, in welchem der Benutzer hinzugefügt wurde
	 * @throws ServerActionErrorException Fehler, der ausgelöst wird, wenn das Hinzufügen fehlschlägt
	 */
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

	/**
	 * Weist ein neues Gerät einem bereits vorhandenen Benutzer zu, indem ein bereits registriertes Gerät angegeben wird
	 * @param oldDeviceID ID eines bereits auf diesen Benutzer registrieren Endgeräts
	 * @param newDeviceID ID des neu zu registrierenden Geräts
	 * @return ID des Benutzers, auf den das neue Gerät registriert wurde
	 * @throws ServerActionErrorException Fehler, der ausgelöst wird, wenn das Hinzufügen fehlschlägt
	 */
	protected int addDeviceToUser(String oldDeviceID, String newDeviceID) throws ServerActionErrorException {
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
