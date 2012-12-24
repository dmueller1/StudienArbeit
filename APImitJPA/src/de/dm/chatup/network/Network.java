package de.dm.chatup.network;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	
	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.setRegistrationRequired(false);
		kryo.register(String[].class);
		kryo.register(String[][].class);
		kryo.register(List.class);
		kryo.register(Contact.class);
		kryo.register(Device.class);
		kryo.register(Chat.class);
		kryo.register(Message.class);
		kryo.register(AddUser.class);
		kryo.register(GetChatsFromUser.class);
//		kryo.register(GetAllMessagesFromChat.class);
		kryo.register(GetFriends.class);
		kryo.register(IsUserExisting.class);
		kryo.register(SendNewMessage.class);
		kryo.register(AddNewChat.class);
		kryo.register(AddUserToChat.class);
//		kryo.register(GetUsersFromChat.class);
		kryo.register(GetDateFromServer.class);
		kryo.register(AddDeviceToUser.class);
	}
	
	static public class Contact {
		public int userid;
		public String vorname;
		public String nachname;
		
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
	}
	
	static public class Device {
		public String deviceID;
		public Contact besitzer;
		
		public Device() {
			
		}
		
		public Device(String deviceID, Contact besitzer) {
			this.deviceID = deviceID;
			this.besitzer = besitzer;
		}
	}
	
	static public class Chat {
		public int chatid;
		public String name;
		public List<Message> messages = new ArrayList<Message>();
		public List<Contact> users = new ArrayList<Contact>();
		
		public Chat() {
			
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
	}
	
	static public class Message {
		public int messageid;
		public Contact ersteller;
		public String erstellDatum;
		public String nachricht;
		
		public Message() {
			
		}

		
		public Message(Chat chat, Contact ersteller, String erstellDat, String nachricht) {
			this.ersteller = ersteller;
			this.erstellDatum = erstellDat;
			this.nachricht = nachricht;
			chat.addMessage(this);
		}

		public int getErstellerID() {
			return ersteller.getUserID();
		}

		public String getErstellDatum() {
			return erstellDatum;
		}

		public String getNachricht() {
			return nachricht;
		}
	}

//	static public class GetUsersFromChat {
//		public int chatID;
//		public List<Contact> result = new ArrayList<Contact>();
//	}

	static public class AddUser {
		public String deviceID;
		public String vorname;
		public String nachname;
		// result = userID
		public Contact result;
	}

	static public class GetChatsFromUser {
		public int userID;
		public List<Chat> result = new ArrayList<Chat>();
	}

//	static public class GetAllMessagesFromChat {
//		public int chatID;
//		public String lastUpdate;
//		public List<Message> result = new ArrayList<Message>();
//	}

	static public class GetFriends {
		// Liste als Array aus userID, deviceID, vorname, nachname
		public List<Contact> result = new ArrayList<Contact>();
	}

	static public class IsUserExisting {
		public String deviceID;
		public int result;
	}

	static public class SendNewMessage {
		public int chatID;
		public int erstellerID;
		public String erstellDatum;
		public String nachricht;
	}

	static public class AddNewChat {
		public String name;
		public List<Contact> users = new ArrayList<Contact>();
		public Chat result;
	}

	static public class AddUserToChat {
		public int chatID;
		public int userID;
	}
	
	static public class GetDateFromServer {
		public String result;
	}
	
	static public class AddDeviceToUser {
		public String oldDeviceID;
		public String newDeviceID;
		public int result;
	}
	
}