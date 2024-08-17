package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import java.io.IOException;
import java.util.List;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	public static String newHost;
	public static int newPort;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		// טיפול בקבלת רשימת סרטים מהשרת
		if (msg instanceof List) {
			List<Movie> movies = (List<Movie>) msg;
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
