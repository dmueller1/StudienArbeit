import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

//This class is a convenient place to keep things common to both the client and server.
public class Network {
	static public final int port = 54555;
	static public final String server = "127.0.0.1";
	static public final String username = "chatuser";
	static public final String password = "702.K_Com&kkX";
	static public final String database = "chat";

	// This registers objects that are going to be sent over the network.
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
		String[][] result;
	}

	static public class GetAllMessagesFromChat {
		public int chatID;
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
}