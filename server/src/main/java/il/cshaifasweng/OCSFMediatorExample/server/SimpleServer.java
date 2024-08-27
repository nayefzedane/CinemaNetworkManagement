package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.PurchaseLink;
import il.cshaifasweng.OCSFMediatorExample.entities.PackageCard;
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
		if(msgString.startsWith("delete_request ")){
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
