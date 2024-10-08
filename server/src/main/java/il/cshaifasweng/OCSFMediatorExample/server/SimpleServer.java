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
		// Check if the message is an instance of Complaints
		if (msg instanceof Complaints) {
			// Cast the incoming message to Complaints
			Complaints complaint = (Complaints) msg;

			// Print or log the received complaint (optional for debugging)
			System.out.println("Received complaint from client: " + complaint.toString());

			// Save the complaint to the data_base using ConnectToDatabase
			//ConnectTo_Database dbHandler = new ConnectToD_atabase();
			ConnectToDatabase.insertComplaint(complaint); // Assuming you have this method defined in ConnectToDatabase

			// Send a response back to the client (optional)
			client.sendToClient("Complaint received and saved successfully.");
		} else {
			// Handle other types of messages if needed
			System.out.println("Received unknown message type.");
		}

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
		//buy with package:
		if (msgString.startsWith("PackageCardRequest:")) {
			// Remove the prefix and split the remaining part of the message by ":"
			String[] parts = msgString.substring("PackageCardRequest:".length()).split(":");

			// Ensure that we have the correct number of parts
			if (parts.length == 4) {
				// Extract the individual values
				int customerId = Integer.parseInt(parts[0]);  // Customer ID
				String packageId = parts[1];                  // Package Card ID
				String seatNumber = parts[2];                 // Seat number
				int movieId = Integer.parseInt(parts[3]);     // Movie ID

				// Now you have the values in separate variables
				System.out.println("Customer ID: " + customerId);
				System.out.println("Package Card ID: " + packageId);
				System.out.println("Seat Number: " + seatNumber);
				System.out.println("Movie ID: " + movieId);
				String answer = ConnectToDatabase.processPackageCard(customerId, Integer.parseInt(packageId), movieId, seatNumber);
				System.out.println(answer);
				if (answer.startsWith("Error")) //if buying with package failed
					client.sendToClient(answer);
				else {//if entering with package succeded
					String[] partsPackage = answer.split(":", 3);  // Split into 3 parts: "Email", email, and the receipt
					String email = partsPackage[1];  // This is the customer's email
					String receiptMessage = partsPackage[2];  // This is the full receipt message
					receiptMessage = receiptMessage + seatNumber + "\n=====================================\n";

					System.out.println("Email to send to: " + email);
					System.out.println("Receipt Message: " + receiptMessage);
					EmailService.sendEmail(email, "Package entrie receipt", receiptMessage);
					client.sendToClient("PackageCardUse:" + receiptMessage);
				}
			} else {
				System.out.println("Invalid PackageCardRequest format.");
			}
		}


// Moaawiayh worked here
		if (msgString.equals("getUnansweredComplaints")) {
			// Fetch only complaints with no answer
			List<Complaints> unansweredComplaints = ConnectToDatabase.getUnansweredComplaints();

			try {
				// Send the list of unanswered complaints back to the client
				client.sendToClient(unansweredComplaints);
			} catch (IOException e) {
				System.err.println("Failed to send unanswered complaints to client: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("Unhandled message: " + msgString);
		}


		if (msgString.startsWith("updateComplaint")) {
			String[] parts = msgString.split(";");
			if (parts.length >= 5) {
				try {
					int complaintId = Integer.parseInt(parts[1]);
					String answer = parts[2];
					double compensation = Double.parseDouble(parts[3]);
					String timeSubmitted = parts[4];

					// Fetch and update the complaint in the database
					Complaints complaint = ConnectToDatabase.getComplaintById(complaintId);
					if (complaint != null) {
						complaint.setAnswer(answer);
						complaint.setFinancialCompensation(compensation);
						complaint.setComplainDate(LocalDateTime.parse(timeSubmitted));
						boolean success = ConnectToDatabase.updateComplaint(complaint);

						// Send response back to client
						if (success) {
							client.sendToClient("updateComplaintResponse;success");
							EmailService.sendEmail(complaint.getMail(), "Complain Responded:" + complaint.getComplainTitle(), complaint.getAnswer() + "\n============\nyour original complain text:\n" +complaint.getComplainText()+"\n============\nfeel free to text us back.");
						} else {
							client.sendToClient("updateComplaintResponse;failure");
						}
					} else {
						client.sendToClient("updateComplaintResponse;not_found");
					}
				} catch (Exception e) {
					e.printStackTrace();
					try {
						client.sendToClient("updateComplaintResponse;error");
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			} else {
				System.out.println("Invalid updateComplaint message format.");
			}
		}

// till here **************************

			



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
			req.setTitle("Update Price");
			req.setDescription(movieTitle + ", Id: " + movieId + ", Showtime: " + movieShowtime + ", Place: " + moviePlace + ", Old price: " + moviePrice + ", New price: " + newPrice);
			ConnectToDatabase.addRequest(req);
			//for checking the link is available
		} else if(msgString.startsWith("CheckLink")){
			System.out.println(msgString);
			String message = msgString; //
			String[] parts = message.split(":");
			String link = parts[1];  // Extract the link part
			String result = ConnectToDatabase.checkLinkByString(link);
			System.out.println(result);
			client.sendToClient(result);

		}
		if (msg instanceof PackageCard) {
			PackageCard packageCard = (PackageCard) msg;
			System.out.println("Received PackageCard from client: " + packageCard.getCustomerEmail());

			// Save the PackageCard to the database and retrieve the generated ID
			PackageCard savedPackageCard = ConnectToDatabase.savePackageCard(packageCard);


			if (savedPackageCard != null) {
				try {
					StringBuilder receiptMessage = new StringBuilder();
					receiptMessage.append("Receipt:\n");
					receiptMessage.append("=====================================\n");
					receiptMessage.append("Package ID: ").append(packageCard.getPackageId()).append("\n");  // Package ID now correctly populated
					receiptMessage.append("Name: ").append(packageCard.getName()).append("\n");
					receiptMessage.append("Customer ID: ").append(packageCard.getCustomerId()).append("\n");
					receiptMessage.append("Customer Email: ").append(packageCard.getCustomerEmail()).append("\n");
					receiptMessage.append("Price: $").append(packageCard.getPrice()).append("\n");
					receiptMessage.append("Remaining Tickets: ").append(packageCard.getRemainingEntries()).append("\n");
					receiptMessage.append("Payment (Last 4 digits): ").append(packageCard.getPaymentLastFourDigits()).append("\n");
					receiptMessage.append("Purchase Date: ").append(packageCard.getPurchaseDate()).append("\n");
					receiptMessage.append("=====================================\n");
					receiptMessage.append("Note: Please remember your purchase ID number ").append(packageCard.getPackageId()).append(", as it will be required when you buy movie tickets.\n");
					EmailService.sendEmail(packageCard.getCustomerEmail(), "Package purchase receipt", receiptMessage.toString());
					client.sendToClient(savedPackageCard);
					System.out.println("PackageCard sent back to client with ID: " + savedPackageCard.getPackageId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (msg instanceof PurchaseLink) {
			PurchaseLink purchaseLink = (PurchaseLink) msg;
			System.out.println("Received PurchaseLink from client: " + purchaseLink.getCustomerMail());

			// Save the PurchaseLink to the database and retrieve the generated ID
			PurchaseLink savedPurchaseLink = ConnectToDatabase.savePurchaseLink(purchaseLink);

			if (savedPurchaseLink != null) {
				try {
					StringBuilder receiptMessage = new StringBuilder();
					receiptMessage.append("Purchase Link Receipt:\n");
					receiptMessage.append("=====================================\n");
					receiptMessage.append("Purchase ID: ").append(savedPurchaseLink.getLinkId()).append("\n");
					receiptMessage.append("Name: ").append(savedPurchaseLink.getName()).append("\n");
					receiptMessage.append("Customer ID: ").append(savedPurchaseLink.getCustomerId()).append("\n");
					receiptMessage.append("Customer Email: ").append(savedPurchaseLink.getCustomerMail()).append("\n");
					receiptMessage.append("Price: $").append(savedPurchaseLink.getPrice()).append("\n");
					receiptMessage.append("Payment (Last 4 digits): ").append(savedPurchaseLink.getPaymentCardLastFour()).append("\n");
					receiptMessage.append("Purchase Date: ").append(savedPurchaseLink.getPurchaseTime()).append("\n");
					receiptMessage.append("Purchase Link: ").append(savedPurchaseLink.getUniqueLink()).append("\n");
					receiptMessage.append("Available from: ").append(purchaseLink.getAvailableFrom()).append("\n");
					receiptMessage.append("=====================================\n");
					receiptMessage.append("Note: Please remember your purchase ID number ").append(savedPurchaseLink.getLinkId()).append(", as it may be required for future transactions.\n");

					// Send receipt email to customer
					EmailService.sendEmail(savedPurchaseLink.getCustomerMail(), "Purchase Link Receipt", receiptMessage.toString());

					// Send the saved PurchaseLink back to the client
					client.sendToClient(receiptMessage.toString());
					System.out.println("PurchaseLink receipt sent back to client with ID: " + savedPurchaseLink.getLinkId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				client.sendToClient("failed Purchase Link buy");
			}
		}


		if (msg instanceof purchaseCard) {
			purchaseCard purchaseCard = (purchaseCard) msg;
			System.out.println("Received purchaseCard from client: " + purchaseCard.getCostMail());

			// Save the purchaseCard to the database and retrieve the generated ID
			purchaseCard savedPurchaseCard = ConnectToDatabase.savePurchaseCard(purchaseCard);

			if (savedPurchaseCard != null) {
				try {
					StringBuilder receiptMessage = new StringBuilder();
					receiptMessage.append("Receipt:\n");
					receiptMessage.append("=====================================\n");
					receiptMessage.append("Purchase ID: ").append(savedPurchaseCard.getOrderId()).append("\n");  // Use purchase ID here
					receiptMessage.append("Name: ").append(savedPurchaseCard.getName()).append("\n");
					receiptMessage.append("Customer ID: ").append(savedPurchaseCard.getCustomerId()).append("\n");
					receiptMessage.append("Customer Email: ").append(savedPurchaseCard.getCostMail()).append("\n");
					receiptMessage.append("Price: $").append(savedPurchaseCard.getPrice()).append("\n");
					receiptMessage.append("Movie Title: ").append(savedPurchaseCard.getMovieTitle()).append("\n");
					receiptMessage.append("Branch Name: ").append(savedPurchaseCard.getBranchName()).append("\n");
					receiptMessage.append("Seat: ").append(savedPurchaseCard.getSeat()).append("\n");
					receiptMessage.append("Showtime: ").append(savedPurchaseCard.getShowTime()).append("\n");
					receiptMessage.append("Payment (Last 4 digits): ").append(savedPurchaseCard.getPaymentCardLastFour()).append("\n");
					receiptMessage.append("Purchase Date: ").append(savedPurchaseCard.getPurchaseDate()).append("\n");
					receiptMessage.append("=====================================\n");
					EmailService.sendEmail(savedPurchaseCard.getCostMail(), "Ticket Purchase Receipt", receiptMessage.toString());

					client.sendToClient(savedPurchaseCard);
					System.out.println("purchaseCard sent back to client with ID: " + savedPurchaseCard.getCustomerId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(msg instanceof Movie){
			ConnectToDatabase.addMovie((Movie) msg);
			System.out.println("Recived movie from Client... Sending Movie to save in database...");
		}
	}
}