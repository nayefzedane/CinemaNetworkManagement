package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.PurchaseLink;
import il.cshaifasweng.OCSFMediatorExample.entities.PackageCard;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaints;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.time.LocalDate;
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
		System.out.println("Received message from client: " + msgString); // Debugging output

		//handle the complaints report
		if (msg.equals("request_complaints_report")) {
			System.out.println("Server: Handling request for complaints report");
			List<Complaints> complaintsList = ConnectToDatabase.getAllComplaintsOrderedByDate();
			try {
				client.sendToClient(complaintsList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//handle the report request
		if (msg.equals("request_ticket_report")) {
			System.out.println("we are on server and we are on request ticket report");
			List<purchaseCard> purchaseList = ConnectToDatabase.getAllPurchasesOrderedByDate();
			System.out.println("we have returned from database to server after getting the tickets");
			try {
				client.sendToClient(purchaseList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (msg.equals("request_purchase_link_report")) {
			System.out.println("Server: Handling request for purchase link report");
			List<PurchaseLink> purchaseLinkList = ConnectToDatabase.getAllPurchaseLinksOrderedByDate();
			try {
				client.sendToClient(purchaseLinkList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (msg.equals("request_package_report")) {
			System.out.println("Server: Handling request for package report");
			List<PackageCard> packageCardList = ConnectToDatabase.getAllPackageCardsOrderedByDate();
			try {
				client.sendToClient(packageCardList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		if (msg.equals("request_price_change_requests")) {
			System.out.println("we are on server and we are on request price change requests");
			List<Request> requestsList = ConnectToDatabase.getAllPriceChangeRequests();
			System.out.println("we have returned from server after getting the requests");
			try {
				client.sendToClient(requestsList);
			} catch (IOException e) {
				System.err.println("Error in getAllPriceChangeRequests: " + e.getMessage());
				e.printStackTrace();
			}
		}
		if (msgString.startsWith("delete_request ")) {
			System.err.println("we are on server and we want to delete");
			System.err.println(msgString);
			String requestId = msgString.substring("delete_request ".length());
			System.err.println(requestId);
			ConnectToDatabase.deleteRequestById(Long.parseLong(requestId));


		}
		if (msgString.startsWith("change_request ")) {
			System.err.println("we are on server and we want to update price");
			System.err.println(msgString);

			// Extract the ID
			String idString = msgString.substring(msgString.indexOf("Id: ") + 4, msgString.indexOf(", Showtime"));
			int movieId = Integer.parseInt(idString.trim());

			// Extract the new price
			String newPriceString = msgString.substring(msgString.indexOf("New price: ") + 11);
			float newPrice = Float.parseFloat(newPriceString.trim());

			// Now you can use the extracted movieId and newPrice
			System.out.println("Movie ID: " + movieId);
			System.out.println("New Price: " + newPrice);

			ConnectToDatabase.updateMoviePrice(movieId, newPrice);

			// Additional implementation if needed
		}
		//return ticket request
		if (msgString.startsWith("Return Ticket")) {
			String[] parts = msgString.split(",");
			String type = parts[1].trim();  // Card or Link
			String orderId = parts[2].trim();
			String customerId = parts[3].trim();
			if (type.equals("Purchase Card")) {
				type = "purchaseCard";
			}
			if (type.equals("Purchase Link")) {
				type = "PurchaseLink";
			}

			// Call the database function to handle return
			String result = ConnectToDatabase.handleReturnTicket(type, orderId, customerId);
			System.out.println(result);
			// Send the result back to the client
			client.sendToClient(result);
		}


		if (msgString.startsWith("login@")) {
			String[] parts = msgString.split("@");
			String username = parts[1];
			String password = parts[2];

			User user = ConnectToDatabase.getUserByCredentials(username, password);

			if (user != null) {
				System.out.println("User authenticated successfully: " + username);  // Debugging output for successful authentication
				client.sendToClient("login_success@" + user.getRole());
			} else {
				System.out.println("Authentication failed: " + username);  // Debugging output for failed authentication
				client.sendToClient("login_failed");
			}
		} else if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Server Warning!");
			client.sendToClient(warning);
			System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
		} else if (msgString.startsWith("get all movies")) {
			List<Movie> movies = ConnectToDatabase.getAllMovies();
			System.out.println("Sending all movies to client, total: " + movies.size()); // Debugging output
			client.sendToClient(movies);
		} else if (msgString.startsWith("update showtime:")) {
			msgString = msgString.substring("update showtime:".length());
			String[] parts = msgString.split(":");
			int movieId = Integer.parseInt(parts[0]);
			String dateStr = parts[1];
			int hour = Integer.parseInt(parts[2]);
			int minute = Integer.parseInt(parts[3]);

			// Convert date and time
			LocalDate date = LocalDate.parse(dateStr);
			LocalTime time = LocalTime.of(hour, minute);
			LocalDateTime newShowtime = LocalDateTime.of(date, time);

			// Update movie showtime in the database
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
						movie.setImageData(value);
						break;
				}
			}
			ConnectToDatabase.addMovie(movie);

		} else if (msgString.startsWith("#getMovieCount")) {
			client.sendToClient("#movieCount:" + ConnectToDatabase.getMovieCountFromDatabase());
		} else if (msgString.startsWith("delete movie ")) {
			String movieId = msgString.substring("delete movie ".length());
			ConnectToDatabase.deleteMovieById(Integer.parseInt(movieId));
		} else if (msgString.startsWith("#getMoviesByOnlineStatus")) {
			boolean isOnline = Boolean.parseBoolean(msgString.substring("#getMoviesByOnlineStatus".length()).trim());
			List<Movie> movies = ConnectToDatabase.getMoviesByOnlineStatus(isOnline);
			System.out.println("Movies fetched by online status (" + isOnline + "): " + movies.size()); // Debugging output
			client.sendToClient(movies);
		} else if (msgString.startsWith("#searchMoviesByAdvancedCriteria")) {
			String[] criteria = msgString.substring("#searchMoviesByAdvancedCriteria;".length()).split(";");
			String cinema = null;
			LocalDate startDate = null;
			LocalDate endDate = null;
			String genre = null;
			String title = null;
			boolean isOnline = false;  // חיפוש תמידי לסרטים לא אונליין

			for (String criterion : criteria) {
				String[] keyValue = criterion.split("=");
				switch (keyValue[0]) {
					case "cinema":
						cinema = keyValue[1];
						break;
					case "startDate":
						startDate = LocalDate.parse(keyValue[1]);
						break;
					case "endDate":
						endDate = LocalDate.parse(keyValue[1]);
						break;
					case "genre":
						genre = keyValue[1];
						break;
					case "title":
						title = keyValue[1];
						break;
				}
			}

			List<Movie> movies = ConnectToDatabase.searchMoviesByAdvancedCriteria(cinema, startDate, endDate, genre, title, isOnline);
			System.out.println("Movies fetched by advanced criteria (non-online): " + movies.size()); // Debugging output
			client.sendToClient(movies);
		} else if (msgString.startsWith("#searchOnlineMoviesByCriteria")) {
			String[] criteria = msgString.substring("#searchOnlineMoviesByCriteria ".length()).split(";");
			String genre = null;
			String title = null;

			for (String criterion : criteria) {
				String[] keyValue = criterion.split("=");
				if (keyValue[0].equals("genre")) {
					genre = keyValue[1];
				} else if (keyValue[0].equals("title")) {
					title = keyValue[1];
				}
			}

			List<Movie> movies = ConnectToDatabase.searchOnlineMoviesByCriteria(genre, title);
			System.out.println("Online movies fetched by criteria (Genre: " + genre + ", Title: " + title + "): " + movies.size()); // Debugging output
			client.sendToClient(movies);
		} else if (msgString.startsWith("#getMoviesByScreeningDate")) {
			String[] dates = msgString.substring("#getMoviesByScreeningDate".length()).trim().split(";");
			LocalDate startDate = null;
			LocalDate endDate = null;

			if (dates.length > 0 && !dates[0].isEmpty()) {
				startDate = LocalDate.parse(dates[0]);
			}
			if (dates.length > 1 && !dates[1].isEmpty()) {
				endDate = LocalDate.parse(dates[1]);
			}

			List<Movie> movies = ConnectToDatabase.getMoviesByScreeningDate(startDate, endDate);
			System.out.println("Movies fetched by screening date: " + movies.size()); // Debugging output
			client.sendToClient(movies);


		} else if (msgString.startsWith("send request:")) {

			String[] parts = msgString.split(":");
			String movieTitle = parts[1];
			String movieId = parts[2];
			String moviePrice = parts[3];
			String newPrice = parts[4];
			String movieShowtime = parts[5];
			String moviePlace = parts[6];

			Request req = new Request();
			req.setTitle("Price update request");
			req.setDescription(movieTitle + ", Id: " + movieId + ", Showtime: " + movieShowtime + ", Place: " + moviePlace + ", Old price: " + moviePrice + ", New price: " + newPrice);
			ConnectToDatabase.addRequest(req);
		}
		if (msg instanceof PackageCard) {
			PackageCard packageCard = (PackageCard) msg;
			System.out.println("Received PackageCard from client: " + packageCard.getCustomerEmail());

			// Save the PackageCard to the database and retrieve the generated ID
			PackageCard savedPackageCard = ConnectToDatabase.savePackageCard(packageCard);

			if (savedPackageCard != null) {
				try {
					// Send the updated PackageCard (with generated ID) back to the client
					client.sendToClient(savedPackageCard);
					System.out.println("PackageCard sent back to client with ID: " + savedPackageCard.getPackageId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}