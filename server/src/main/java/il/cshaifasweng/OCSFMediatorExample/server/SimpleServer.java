package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
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
		} else if (msgString.startsWith("update showtime:")){
			msgString = msgString.substring("update showtime:".length());
			String[] parts = msgString.split(":");
			int movieId = Integer.parseInt(parts[0]);
			String dateStr = parts[1];
			int hour = Integer.parseInt(parts[2]);
			int minute = Integer.parseInt(parts[3]);

			// Parse date and time
			LocalDate date = LocalDate.parse(dateStr);
			LocalTime time = LocalTime.of(hour, minute);
			LocalDateTime newShowtime = LocalDateTime.of(date, time);

			// Update the movie's showtime in the database
			ConnectToDatabase.updateMovieShowtimeInDatabase(movieId, newShowtime);
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

		}
		else if (msgString.startsWith("#getMovieCount")) {
			client.sendToClient("#movieCount:" + ConnectToDatabase.getMovieCountFromDatabase());
		}
		else if (msgString.startsWith("delete movie ")) {
			String movieId = msgString.substring("delete movie ".length());
			ConnectToDatabase.deleteMovieById(Integer.parseInt(movieId));
		}

		else if (msgString.startsWith("send request:")){

			String[] parts = msgString.split(":");
			String movieTitle = parts[1];
			String movieId = parts[2];
			String moviePrice = parts[3];
			String newPrice = parts[4];
			String movieShowtime = parts[5];
			String moviePlace = parts[6];

			Request req = new Request();
			req.setTitle("Price update request");
			req.setDescription(movieTitle+", Id: "+movieId + ", Showtime: "+ movieShowtime + ", Place: " + moviePlace +", Old price: " +moviePrice+", New price: "+newPrice);
			ConnectToDatabase.addRequest(req);
		}
	}
}