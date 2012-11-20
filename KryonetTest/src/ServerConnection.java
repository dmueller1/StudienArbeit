import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class ServerConnection {

	private static ServerConnection instance = null;
	private int userID;
	public String test;

	protected ServerConnection() {

	}

	public static ServerConnection getInstance() {
		if (instance == null) {
			instance = new ServerConnection();
		}
		return instance;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int isUserRegistered(String deviceID) {
		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("deviceid", "UTF-8") + "="
					+ URLEncoder.encode(deviceID, "UTF-8");
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/isUserExisting.php";
			String request = "http://" + Network.server + "/chat/isUserExisting.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String line = "0";
			String result = "";

			if ((line = reader.readLine()) != null) {
				result = line;
			}

			return Integer.valueOf(result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int addNewUser(String deviceID, String vorname, String nachname) {

		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("deviceid", "UTF-8") + "="
					+ URLEncoder.encode(deviceID, "UTF-8") + "&"
					+ URLEncoder.encode("vorname", "UTF-8") + "="
					+ URLEncoder.encode(vorname, "UTF-8") + "&"
					+ URLEncoder.encode("nachname", "UTF-8") + "="
					+ URLEncoder.encode(nachname, "UTF-8");
			// String request = "http://dmmueller1.dyndns-web.com/chat/addUser.php";
			String request = "http://" + Network.server + "/chat/addUser.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String result = sb.toString();

			connection.disconnect();

			return Integer.valueOf(result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int sendNewMessage(int chatID, int userID, String datum,
			String nachricht) {

		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("chatID", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(chatID), "UTF-8") + "&"
					+ URLEncoder.encode("erstellerID", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(userID), "UTF-8") + "&"
					+ URLEncoder.encode("datum", "UTF-8") + "="
					+ URLEncoder.encode(datum, "UTF-8") + "&"
					+ URLEncoder.encode("nachricht", "UTF-8") + "="
					+ URLEncoder.encode(nachricht, "UTF-8");
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/sendNewMessage.php";
			String request = "http://" + Network.server + "/chat/sendNewMessage.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String result = sb.toString();

			connection.disconnect();

			return Integer.valueOf(result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public String[][] getFriends() {

		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8");
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/getFriends.php";
			String request = "http://" + Network.server + "/chat/getFriends.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			connection.disconnect();
			String result = sb.toString();

			if (!result.startsWith("-1")) {

				JSONParser parser = new JSONParser();
				JSONArray jsonEintraege = (JSONArray) parser.parse(result);

				String[][] friends = new String[jsonEintraege.size()][3];

				for (int zaehler = 0; zaehler < jsonEintraege.size(); zaehler++) {
					friends[zaehler][0] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("UserID"));
					friends[zaehler][1] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("Vorname"));
					friends[zaehler][2] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("Nachname"));
				}

				return friends;
			} else {
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public String[][] getAllMessagesFromChat(int chatID, String lastUpdateDate) {
		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("chatID", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(chatID), "UTF-8");
			
			if(lastUpdateDate != null) {
				urlParameters += "&" 
						+ URLEncoder.encode("lastUpdate", "UTF-8") + "="
						+ URLEncoder.encode(lastUpdateDate, "UTF-8");
			} else {
				urlParameters += "&" 
						+ URLEncoder.encode("lastUpdate", "UTF-8") + "="
						+ URLEncoder.encode("2000-01-01", "UTF-8");
			}
			
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/getAllMessagesFromChat.php";
			String request = "http://" + Network.server + "/chat/getAllMessagesFromChat.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			connection.disconnect();
			String result = sb.toString();

			if (!result.startsWith("-1")) {

				JSONParser parser = new JSONParser();
				JSONArray jsonEintraege = (JSONArray) parser.parse(result);

				String[][] messages = new String[jsonEintraege.size()][4];

				for (int zaehler = 0; zaehler < jsonEintraege.size(); zaehler++) {
					messages[zaehler][0] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("ChatID"));
					messages[zaehler][1] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("ErstellerID"));
					messages[zaehler][2] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("ErstellDatum"));
					messages[zaehler][3] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("Nachricht"));
				}

				return messages;
			} else {
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] getUserIDsFromChat(int chatID) {
		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("chatID", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(chatID), "UTF-8");
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/getUserIDsFromChat.php";
			String request = "http://" + Network.server + "/chat/getUserIDsFromChat.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			connection.disconnect();
			String result = sb.toString();

			if (!result.startsWith("-1")) {

				JSONParser parser = new JSONParser();
				JSONArray jsonEintraege = (JSONArray) parser.parse(result);

				String[] userIDsChats = new String[jsonEintraege.size()];

				for (int zaehler = 0; zaehler < jsonEintraege.size(); zaehler++) {
					userIDsChats[zaehler] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("UserID"));
				}

				return userIDsChats;
			} else {
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[][] getChatsFromUser(int userID) {
		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("userID", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(userID), "UTF-8");
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/getAllMessagesFromChat.php";
			String request = "http://" + Network.server + "/chat/getAllChatsFromUser.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			connection.disconnect();
			String result = sb.toString();

			if (!result.startsWith("-1")) {

				JSONParser parser = new JSONParser();
				JSONArray jsonEintraege = (JSONArray) parser.parse(result);

				String[][] chats = new String[jsonEintraege.size()][2];

				for (int zaehler = 0; zaehler < jsonEintraege.size(); zaehler++) {
					chats[zaehler][0] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("ID"));
					chats[zaehler][1] = String.valueOf(((JSONObject) jsonEintraege
							.get(zaehler)).get("Name"));
				}

				return chats;
			} else {
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public int createNewChat(String name, String[] userIDs) {
		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("name", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(name), "UTF-8");
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/getAllMessagesFromChat.php";
			String request = "http://" + Network.server + "/chat/addNewChat.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			connection.disconnect();
			int result = Integer.valueOf(sb.toString());

			if(result > -1) {
				
				for(int i = 0; i < userIDs.length; i++) {
					int erfolg = addUserToChat(result, userIDs[i]);
					if(erfolg <= -1) {
						return -1;
					}
				}
				
				return result;
				
			} else {
				return -1;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int addUserToChat(int chatID, String userID) {
		try {
			String urlParameters = URLEncoder.encode("username", "UTF-8") + "="
					+ URLEncoder.encode(Network.username, "UTF-8") + "&"
					+ URLEncoder.encode("password", "UTF-8") + "="
					+ URLEncoder.encode(Network.password, "UTF-8") + "&"
					+ URLEncoder.encode("database", "UTF-8") + "="
					+ URLEncoder.encode(Network.database, "UTF-8") + "&"
					+ URLEncoder.encode("userID", "UTF-8") + "="
					+ URLEncoder.encode(userID, "UTF-8") + "&"
					+ URLEncoder.encode("chatID", "UTF-8") + "="
					+ URLEncoder.encode(String.valueOf(chatID), "UTF-8");
			// String request =
			// "http://dmmueller1.dyndns-web.com/chat/getAllMessagesFromChat.php";
			String request = "http://" + Network.server + "/chat/addUserToChat.php";
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			connection.disconnect();
			int result = Integer.valueOf(sb.toString());

			return result;

		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
