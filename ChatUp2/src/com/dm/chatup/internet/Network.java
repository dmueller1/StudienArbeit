package com.dm.chatup.internet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	static public final int port = 54555;
	static public final String server = "dmmueller1.dyndns-web.com";
	//static public final String server = "192.168.1.51";
	//static public final String server = "172.16.26.172";

	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(String[].class);
		kryo.register(String[][].class);
		kryo.register(AddUser.class);
		kryo.register(GetChatsFromUser.class);
		kryo.register(GetAllMessagesFromChat.class);
		kryo.register(GetFriends.class);
		kryo.register(IsUserExisting.class);
		kryo.register(SendNewMessage.class);
		kryo.register(AddNewChat.class);
		kryo.register(AddUserToChat.class);
		kryo.register(GetUserIDsFromChat.class);
		kryo.register(GetDateFromServer.class);
	}

	static public class GetUserIDsFromChat {
		public int chatID;
		public String[] result;
	}

	static public class AddUser {
		public String deviceID;
		public String vorname;
		public String nachname;
		// result = userID
		public int result;
	}

	static public class GetChatsFromUser {
		public int userID;
		public String[][] result;
	}

	static public class GetAllMessagesFromChat {
		public int chatID;
		public String lastUpdate;
		public String[][] result;
	}

	static public class GetFriends {
		// Liste als Array aus userID, deviceID, vorname, nachname
		public String[][] result;
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
		public String[] userIDs;
		public int result;
	}

	static public class AddUserToChat {
		public int chatID;
		public int userID;
	}
	
	static public class GetDateFromServer {
		public String result;
	}
	
}
