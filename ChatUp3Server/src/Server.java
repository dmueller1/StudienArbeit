import de.dm.chatup.server.ChatUpServer;



public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ChatUpServer cus = new ChatUpServer(54555, "127.0.0.1", "root", "24.Stunden", "chat");
		System.out.println(cus.getStatus());
		
	}

}
