package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;
import il.cshaifasweng.OCSFMediatorExample.entities.PurchaseLink;
import il.cshaifasweng.OCSFMediatorExample.entities.PackageCard;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaints;
import javafx.application.Platform;
import il.cshaifasweng.OCSFMediatorExample.client.ContentManagerController;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;



public class SimpleClient extends AbstractClient {

	static ContentManagerController contentManagerController;
	private static SimpleClient client = null;
	public static String newHost;
	public static int newPort;
	static AdminController adminController;
	//static price_change_requestsController priceChangeRequestsController;
	public void setAdminController(AdminController adminController) {
		this.adminController = adminController;
		System.out.println("AdminController set: " + (adminController != null));
	}





	private SimpleClient(String host, int port) {
		super(host, port);
	}

	public void setContentManagerController(ContentManagerController controller) {
		this.contentManagerController = controller;
	}

	// הוספת פונקציה לשליחת בקשה לשרת לקבלת סרטים לפי הסטטוס של ONLINE
	public void requestMoviesByOnlineStatus(boolean isOnline) {
		try {
			System.out.println("Requesting movies with online status: " + isOnline); // Debugging output
			sendToServer("#getMoviesByOnlineStatus " + isOnline);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void requestMoviesByAdvancedCriteria(String cinema, LocalDate startDate, LocalDate endDate, String genre, String title, boolean isOnline) {
		try {
			StringBuilder request = new StringBuilder("#searchMoviesByAdvancedCriteria;");

			if (cinema != null && !cinema.isEmpty()) {
				request.append("cinema=").append(cinema).append(";");
			}
			if (startDate != null) {
				request.append("startDate=").append(startDate.toString()).append(";");
			}
			if (endDate != null) {
				request.append("endDate=").append(endDate.toString()).append(";");
			}
			if (genre != null && !genre.isEmpty()) {
				request.append("genre=").append(genre).append(";");
			}
			if (title != null && !title.isEmpty()) {
				request.append("title=").append(title).append(";");
			}

			request.append("isOnline=").append(isOnline).append(";");

			System.out.println("Requesting movies by advanced criteria: " + request.toString()); // Debugging output
			sendToServer(request.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void requestMoviesByCriteria(String cinema, LocalDate date, String title) {
		try {
			String request = "#searchMoviesByCriteria isOnline=false;";
			if (cinema != null) {
				request += "cinema=" + cinema + ";";
			}
			if (date != null) {
				request += "date=" + date.toString() + ";";
			}
			if (title != null && !title.isEmpty()) {
				request += "title=" + title + ";";
			}
			System.out.println("Requesting movies by criteria: " + request); // Debugging output
			sendToServer(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// הוספת פונקציה לשליחת בקשה לשרת לפי קריטריונים של חיפוש לסרטים אונליין
	public void requestOnlineMoviesByCriteria(String genre, String title) {
		try {
			String request = "#searchOnlineMoviesByCriteria ";
			if (genre != null) {
				request += "genre=" + genre + ";";
			}
			if (title != null && !title.isEmpty()) {
				request += "title=" + title + ";";
			}
			System.out.println("Requesting online movies by criteria: " + request); // Debugging output
			sendToServer(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleMessageFromServer(Object msg) {

		// Check if the message is a list
		if (msg instanceof List<?>) {
			List<?> list = (List<?>) msg;

			// Check if it's a list of movies
			if (!list.isEmpty() && list.get(0) instanceof Movie) {
				List<Movie> movies = (List<Movie>) msg;
				System.out.println("Movies received from server: " + movies.size()); // Debugging output
				Platform.runLater(() -> {
					try {
						MainWindowController mainController = (MainWindowController) App.getController();
						Object activeController = mainController.getActiveController();

						if (activeController instanceof OfflineMoviesController) {
							OfflineMoviesController controller = (OfflineMoviesController) activeController;
							controller.displayMovies(movies);
							controller.setUpcomingMovies(movies);  // הוספת קריאה לפונקציה setUpcomingMovies
						} else if (activeController instanceof OnlineMoviesController) {
							((OnlineMoviesController) activeController).displayMovies(movies);
						} else {
							System.out.println("No appropriate controller found to display movies.");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				ContentManagerController controller = (ContentManagerController) App.getController();
				controller.updateMovieTable(movies);
			}

			// Check if it's a list of purchase cards
			else if (!list.isEmpty() && list.get(0) instanceof purchaseCard) {
				System.out.println("we are in simple client after we get back from server, purchase card");
				List<purchaseCard> purchaseList = (List<purchaseCard>) list;
				System.out.println("Purchase report received from server: " + purchaseList.size()); // Debugging output
				Platform.runLater(() -> {
					AdminController controller = (AdminController) App.getController();
					controller.updatePurchaseList(purchaseList);
				});
			}
			if (!list.isEmpty() && list.get(0) instanceof PurchaseLink) {
				System.out.println("Client: Received purchase link report from server");
				List<PurchaseLink> purchaseLinkList = (List<PurchaseLink>) list;
				Platform.runLater(() -> {
					AdminController controller = (AdminController) App.getController();
					controller.updatePurchaseLinkList(purchaseLinkList);
				});
			}
			if (!list.isEmpty() && list.get(0) instanceof PackageCard) {
				System.out.println("Client: Received package report from server");
				List<PackageCard> packageCardList = (List<PackageCard>) list;
				Platform.runLater(() -> {
					AdminController controller = (AdminController) App.getController();
					controller.updatePackageList(packageCardList);
				});
			} else if (!list.isEmpty() && list.get(0) instanceof Request) {
				System.out.println("on simple client requests");
				List<Request> requests = (List<Request>) list;
				System.out.println("Price change requests received from server: " + requests.size());
				Platform.runLater(() -> {
					try {
						AdminController controller = (AdminController) App.getController();
						if (controller != null) {
							controller.updateRequestList(requests);
						}

					} catch (Exception e) {
						e.printStackTrace();

					}
				});
				// to check here later if reports not working again
			} else if (!list.isEmpty() && list.get(0) instanceof Complaints) {
				System.out.println("Client: Received complaints report from server");
				List<Complaints> complaintsList = (List<Complaints>) list;
				Platform.runLater(() -> {
					try {
						Object controller = App.getController();

						// Check if the controller is an AdminController
						if (controller instanceof AdminController) {
							AdminController adminController = (AdminController) controller;
							System.out.println("Error: 1");
							adminController.updateComplaintsList(complaintsList); // Use the appropriate method for AdminController
						} else if (controller instanceof CustomerServiceController) {
							CustomerServiceController customerServiceController = (CustomerServiceController) controller;
							System.out.println("Error: 2");
							customerServiceController.handleReceivedComplaints(complaintsList); // Use the appropriate method for CustomerServiceController
						} else {
							System.out.println("Error: The active controller is not an instance of AdminController or CustomerServiceController.");
							System.out.println("Actual controller type: " + controller.getClass().getName());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}


		}

		// טיפול בקבלת רשימת סרטים מהשרת


		// טיפול בתשובת ההתחברות מהשרת
		if (msg instanceof String) {
			String response = (String) msg;
			Platform.runLater(() -> {
				if (response.startsWith("login_success@")) {
					String role = response.split("@")[1];
					try {
						// הפניה לחלון המתאים לפי תפקיד המשתמש
						switch (role) {
							case "Admin":
								App.setRoot("admin_dashboard");
								break;
							case "Manager":
								App.setRoot("content_manager_dashboard");
								break;
							case "Customer":
								App.setRoot("customer_dashboard");
								break;
							case "CustomerService":
								App.setRoot("customer_service_dashboard");
								break;
							case "admin haifa":
								App.setRoot("admin_haifa");
								break;
							case "admin nazareth":
								App.setRoot("admin_nazareth");
								break;
							default:
								throw new IllegalArgumentException("Unknown role: " + role);

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try{ // this function is to know whether it is the admin or one of the branches admin, because we made the same controller for all of them
						System.out.println("we are setting the view type now:");
						AdminController controller  = (AdminController) App.getController();
						if (controller != null) {
							controller.setViewType(role);
						}

					} catch(Exception e){
						e.printStackTrace();

					}
				} else if (response.equals("login_failed")) {
					// הודעת שגיאה על כישלון בהתחברות
					System.out.println("Login failed.");
				} else if (response.startsWith("#movieCount")) {
					int count = Integer.parseInt(response.split(":")[1]);
					if (contentManagerController != null) {
						contentManagerController.setMovieCount(count);
					}
				}
				// Handle the return ticket message
				else if (response.startsWith("Return Ticket succeeded")) {
					String[] parts = response.split(" ");
					float refundAmount = Float.parseFloat(parts[3]); // extract the refund amount
					ReturnTicket.showSuccessAlert(refundAmount); // Show success alert with the refund amount
				} else if (response.startsWith("Return Ticket failed")) {
					ReturnTicket.showFailureAlert(response); // Show failure alert with the error message
				}
			});
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(newHost, newPort);  // התחברות לשרת
			try {
				client.openConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return client;
	}



}
