package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleServer extends AbstractServer {

	public SimpleServer(int port) {
		super(port);
		
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) throws Exception {
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (msgString.startsWith("get all movies")) {
			List<Movie> movies=ConnectToDatabase.getAllMovies();
			client.sendToClient(movies);
		}
		if(msgString.startsWith("Update time @")) {
			String[] parts = msgString.split("@");
			LocalTime time=LocalTime.parse(parts[1]);
			ConnectToDatabase.updateShowtime(parts[2],time);
			List<Movie> movies=ConnectToDatabase.getAllMovies();
			client.sendToClient(movies);
		}

	}

}
