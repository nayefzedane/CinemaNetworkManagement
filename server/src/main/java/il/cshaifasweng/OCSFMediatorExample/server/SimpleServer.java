package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SimpleServer extends AbstractServer {

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) throws Exception {
		String msgString = msg.toString();

		if (msgString.startsWith("login@")) {
			String[] parts = msgString.split("@");
			String username = parts[1];
			String password = parts[2];

			User user = ConnectToDatabase.getUserByCredentials(username, password);

			if (user != null) {
				System.out.println("User authenticated: " + username);  // הדפסה אם המשתמש אומת בהצלחה
				client.sendToClient("login_success@" + user.getRole());
			} else {
				System.out.println("User authentication failed: " + username);  // הדפסה אם המשתמש לא אומת
				client.sendToClient("login_failed");
			}
		} else if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			client.sendToClient(warning);
			System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
		} else if (msgString.startsWith("get all movies")) {
			List<Movie> movies = ConnectToDatabase.getAllMovies();
			client.sendToClient(movies);
		} else if (msgString.startsWith("Update time @")) {
			String[] parts = msgString.split("@");
			LocalTime time = LocalTime.parse(parts[1]);

			// ממיר את LocalTime ל-LocalDateTime עם התאריך הנוכחי
			LocalDateTime dateTime = LocalDateTime.now().with(time);

			ConnectToDatabase.updateShowtime(parts[2], dateTime);
			List<Movie> movies = ConnectToDatabase.getAllMovies();
			client.sendToClient(movies);
		}
	}
}
