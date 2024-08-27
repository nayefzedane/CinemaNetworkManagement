package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;
import il.cshaifasweng.OCSFMediatorExample.entities.PurchaseLink;
import il.cshaifasweng.OCSFMediatorExample.entities.PackageCard;

import javafx.application.Platform;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import java.io.IOException;
import java.util.List;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;



public class SimpleClient extends AbstractClient {

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

	@Override
	protected void handleMessageFromServer(Object msg) {
		// Check if the message is a list
		if (msg instanceof List<?>) {
			List<?> list = (List<?>) msg;

			// Check if it's a list of movies
			if (!list.isEmpty() && list.get(0) instanceof Movie) {
				List<Movie> movies = (List<Movie>) list;
				System.out.println("Movies received from server: " + movies.size()); // Debugging output
				Platform.runLater(() -> {
					try {
						CustomerController controller = (CustomerController) App.getController();
						if (controller != null) {
							controller.displayMovies(movies);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			// Check if it's a list of purchase cards
			else if (!list.isEmpty() && list.get(0) instanceof purchaseCard) {
				System.out.println("we are in simple client after we get back from server, purchase card");
				List<purchaseCard> purchaseList = (List<purchaseCard>) list;
				System.out.println("Purchase report received from server: " + purchaseList.size()); // Debugging output
				Platform.runLater(() -> {
					AdminController controller  = (AdminController) App.getController();
					controller.updatePurchaseList(purchaseList);
				});
			}
			if (!list.isEmpty() && list.get(0) instanceof PurchaseLink) {
				System.out.println("Client: Received purchase link report from server");
				List<PurchaseLink> purchaseLinkList = (List<PurchaseLink>) list;
				Platform.runLater(() -> {
					AdminController controller  = (AdminController) App.getController();
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
			}

			else if (!list.isEmpty() && list.get(0) instanceof Request) {
				System.out.println("on simple client requests");
				List<Request> requests = (List<Request>) list;
				System.out.println("Price change requests received from server: " + requests.size());
				Platform.runLater(() -> {
					try{
						AdminController controller  = (AdminController) App.getController();
						if (controller != null) {
							controller.updateRequestList(requests);
					}

					} catch(Exception e){
						e.printStackTrace();

					}
				});
			}
		}

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
								App.setRoot("manager_dashboard");
								break;
							case "Customer":
								App.setRoot("customer_dashboard");
								break;
							case "CustomerService":
								App.setRoot("customer_service_dashboard");
								break;
							default:
								throw new IllegalArgumentException("Unknown role: " + role);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (response.equals("login_failed")) {
					// הודעת שגיאה על כישלון בהתחברות
					System.out.println("Login failed.");
				}
			});
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(newHost, newPort);  // התחברות לשרת
		}
		return client;
	}





}
