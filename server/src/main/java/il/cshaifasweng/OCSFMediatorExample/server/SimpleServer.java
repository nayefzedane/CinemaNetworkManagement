package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
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
		} else if (msgString.startsWith("id=")) {
			String movieString = msg.toString();
			String[] fields = movieString.split(";");
			Movie movie = new Movie();

			for (String field : fields) {
				String[] keyValue = field.split("=");
				String key = keyValue[0];
				String value = keyValue[1];

				switch (key) {
					case "id":
						movie.setId(Integer.parseInt(value));
						break;
					case "title":
						movie.setTitle(value);
						break;
					case "showtime":
						movie.setShowtime(LocalDateTime.parse(value));
						break;
					case "releaseDate":
						movie.setReleaseDate(LocalDate.parse(value));
						break;
					case "genre":
						movie.setGenre(value);
						break;
					case "duration":
						movie.setDuration(Integer.parseInt(value));
						break;
					case "rating":
						movie.setRating(Float.parseFloat(value));
						break;
					case "director":
						movie.setDirector(value);
						break;
					case "description":
						movie.setDescription(value);
						break;
					case "imagePath":
						movie.setImagePath(value);
						break;
					case "place":
						movie.setPlace(value);
						break;
					case "price":
						movie.setPrice(Float.parseFloat(value));
						break;
					case "isOnline":
						movie.setOnline(Boolean.parseBoolean(value));
						break;
					case "availableSeat":
						movie.setAvailableSeat(Integer.parseInt(value));
						break;
					case "hallNumber":
						movie.setHallNumber(Integer.parseInt(value));
						break;
					case "imageData":
						movie.setImageData((value));
						break;
					// Handle other fields similarly...
				}
			}
		ConnectToDatabase.addMovie(movie);

		} else if (msgString.startsWith("#getMovieCount")) {
				client.sendToClient("#movieCount:" + ConnectToDatabase.getMovieCountFromDatabase() );
		}

	}
}