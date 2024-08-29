package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import il.cshaifasweng.OCSFMediatorExample.client.ContentManagerController;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class SimpleClient extends AbstractClient {

	static ContentManagerController contentManagerController;
	private static SimpleClient client = null;
	public static String newHost;
	public static int newPort;

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

	public void requestMoviesByAdvancedCriteria(String cinema, LocalDate startDate, LocalDate endDate, String genre, String title) {
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
		// טיפול בקבלת רשימת סרטים מהשרת
		if (msg instanceof List) {
			List<Movie> movies = (List<Movie>) msg;
			System.out.println("Movies received from server: " + movies.size()); // Debugging output
			Platform.runLater(() -> {
				try {
					MainWindowController mainController = (MainWindowController) App.getController();
					Object activeController = mainController.getActiveController();

					if (activeController instanceof OfflineMoviesController) {
						((OfflineMoviesController) activeController).displayMovies(movies);
					} else if (activeController instanceof OnlineMoviesController) {
						((OnlineMoviesController) activeController).displayMovies(movies);
					} else {
						System.out.println("No appropriate controller found to display movies.");
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
				} else if (response.startsWith("#movieCount")) {
					int count = Integer.parseInt(response.split(":")[1]);
					if (contentManagerController != null) {
						contentManagerController.setMovieCount(count);
					}
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
