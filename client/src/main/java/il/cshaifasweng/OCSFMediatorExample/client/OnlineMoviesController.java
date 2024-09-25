package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.PurchaseLink;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;

import java.io.ByteArrayInputStream;
import java.util.List;
import javafx.scene.control.Alert;
import java.time.LocalDateTime;
import java.io.IOException;

public class OnlineMoviesController {

    @FXML
    private StackPane mainWindowRoot;  // שורש החלון הראשי

    @FXML
    private TilePane movieTilePane;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private VBox searchWindow;

    @FXML
    private Button searchButton;

    private Stage detailsStage;  // חלון הפרטים
    private GaussianBlur blurEffect = new GaussianBlur(20);  // טשטוש החלון הראשי

    @FXML
    public void initialize() {
        // Adding genre options to the ComboBox
        genreComboBox.getItems().addAll("ALL", "Action", "Drama", "Comedy", "Horror", "Documentary");

        // Adding listener to search field for real-time filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchMovies());

        // Adding listener to genre ComboBox to trigger movie search when a genre is selected
        genreComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            searchMovies();  // Perform search when genre changes
        });

        // Display all online movies when the window is initialized
        showAllOnlineMovies();

        // Initially hide the search window
        searchWindow.setVisible(false);
        searchWindow.setManaged(false);
    }

    @FXML
    public void showAllOnlineMovies() {
        SimpleClient client = SimpleClient.getClient();
        client.requestMoviesByOnlineStatus(true);  // Request movies with online status
    }

    @FXML
    public void searchMovies() {
        String selectedGenre = genreComboBox.getValue();
        String movieTitle = searchField.getText();

        // Treat "ALL" as no genre filter
        if ("ALL".equals(selectedGenre)) {
            selectedGenre = null;
        }

        SimpleClient client = SimpleClient.getClient();
        System.out.println("Searching movies with criteria: Genre = " + selectedGenre + ", Title = " + movieTitle);
        client.requestOnlineMoviesByCriteria(selectedGenre, movieTitle);
    }
    public void displayMovies(List<Movie> movies) {
        movieTilePane.getChildren().clear();  // Clear the current movie tiles

        if (movies == null || movies.isEmpty()) {
            Text noMoviesText = new Text("No movies found.");
            movieTilePane.getChildren().add(noMoviesText);  // Display a "No movies found" message
        } else {
            for (Movie movie : movies) {
                VBox movieBox = new VBox(10);  // מרווח גדול יותר בין התמונה לשם הסרט
                ImageView movieImage = new ImageView(new Image(movie.getImagePath()));

                if (movie.getImageData() != null && movie.getImageData().length > 0) {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(movie.getImageData());
                    movieImage = new ImageView(new Image(inputStream));
                }

                // הגדרת גודל התמונה
                movieImage.setFitHeight(300);  // גובה גדול יותר
                movieImage.setFitWidth(200);   // רוחב גדול יותר
                movieImage.setPreserveRatio(true);  // שמירת יחס התמונה

                Text movieTitle = new Text(movie.getTitle());  // Movie title

                // Adding a "View More" button to display additional details
                Button viewMoreButton = new Button("View More");
                viewMoreButton.setOnAction(event -> showMovieDetails(movie));  // Open movie details on click

                movieBox.getChildren().addAll(movieImage, movieTitle, viewMoreButton);  // Add components to VBox
                movieTilePane.getChildren().add(movieBox);  // Add the VBox to the TilePane
            }
        }
    }

    private void showMovieDetails(Movie movie) {
        // יצירת VBox שמכיל את כל פרטי הסרט
        VBox movieDetailsBox = new VBox(20);
        movieDetailsBox.getStyleClass().add("movie-details");
        movieDetailsBox.setPadding(new Insets(20));

        // יצירת כפתור סגירה בצד ימין
        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> {
            detailsStage.close(); // סוגר את החלון
            mainWindowRoot.setEffect(null); // מסיר את אפקט הטשטוש
        });

        HBox titleBar = new HBox(closeButton);
        titleBar.setAlignment(Pos.TOP_RIGHT); // יישור כפתור הסגירה לימין למעלה

        // הוספת תמונת הסרט
        ImageView movieImage;
        if (movie.getImageData() != null) {
            Image image = new Image(new ByteArrayInputStream(movie.getImageData()));
            movieImage = new ImageView(image);
        } else {
            movieImage = new ImageView(new Image(movie.getImagePath()));
        }
        movieImage.setFitHeight(300);
        movieImage.setFitWidth(400);
        movieImage.setPreserveRatio(true); // שמירת יחס התמונה

        // פרטים נוספים של הסרט
        Text movieTitle = new Text(movie.getTitle());
        movieTitle.getStyleClass().add("movie-title");

        Text movieDescription = new Text("Description: " + movie.getDescription());
        movieDescription.getStyleClass().add("movie-description");
        movieDescription.setWrappingWidth(400);

        Text movieGenre = new Text("Genre: " + movie.getGenre());
        Text movieDuration = new Text("Duration: " + movie.getDuration() + " minutes");
        Text movieRating = new Text("Rating: " + movie.getRating());
        Text movieDirector = new Text("Director: " + movie.getDirector());
        Text moviePrice = new Text("Price: $" + movie.getPrice());
        Text movieIsOnline = new Text("Available Online: " + (movie.isOnline() ? "Yes" : "No"));
        Text movieProducer = new Text("Producer: " + movie.getProducer());
        Text movieLeadingActors = new Text("LeadingActors: " + movie.getLeadingActors());

        // כפתור "BUY LINK"
        Button buyLinkButton = new Button("Buy Link");
        buyLinkButton.getStyleClass().add("buy-button");
        buyLinkButton.setOnAction(e -> {
            detailsStage.close();  // Close the movie details window
            openLinkPaymentWindow(movie);  // Open the payment window for the link
        });

        // סידור הפרטים בתוך VBox
        VBox movieInfo = new VBox(10,
                movieTitle,
                movieDescription,
                movieGenre,
                movieDuration,
                movieRating,
                movieDirector,
                moviePrice,
                movieProducer,
                movieLeadingActors,
                movieIsOnline,
                buyLinkButton  // הוספת כפתור התשלום עבור הלינק
        );
        movieInfo.getStyleClass().add("movie-info");
        movieInfo.setAlignment(Pos.CENTER_LEFT); // יישור לשמאל

        movieDetailsBox.getChildren().addAll(titleBar, movieImage, movieInfo);

        // הוספת ScrollPane כדי לאפשר גלילה רק מעלה ומטה
        ScrollPane scrollPane = new ScrollPane(movieDetailsBox);
        scrollPane.setFitToWidth(true);  // התאמת התוכן לרוחב
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  // ביטול הגלילה האופקית
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // גלילה אנכית לפי הצורך

        // יצירת חלון חדש עם התצוגה של פרטי הסרט
        detailsStage = new Stage();
        detailsStage.setTitle("Movie Details");

        // קביעת גודל החלון, ואפשרות גלילה
        Scene scene = new Scene(scrollPane, 700, 600);
        scene.getStylesheets().add(getClass().getResource("OfflineMovies.css").toExternalForm());

        // הוספת הטשטוש לחלון הראשי באופן מיידי
        mainWindowRoot.setEffect(blurEffect);

        // הסרת הטשטוש כשסוגרים את החלון
        detailsStage.setOnCloseRequest(event -> mainWindowRoot.setEffect(null));

        // הגדרת החלון הדינאמי שיהיה קבוע ולא ניתן להזזה
        detailsStage.initStyle(StageStyle.UNDECORATED);
        detailsStage.setResizable(false); // לא ניתן לשנות גודל

        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void sendPurchaseLinkToServer(PurchaseLink purchaseLink) {
        try {
            SimpleClient.getClient().sendToServer(purchaseLink);
        } catch (IOException e) {
            e.printStackTrace();  // תוכל להציג הודעת שגיאה אחרת במקום להדפיס את ה-Stack Trace
            showAlert(Alert.AlertType.ERROR, "Communication Error", "Failed to send purchase link to server.");
        }
    }

    // חלון התשלום עבור רכישת לינק
    private void openLinkPaymentWindow(Movie movie) {
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Buy Link");

        // Create the VBox for payment details
        VBox paymentDetails = new VBox(10);
        paymentDetails.setPadding(new Insets(20));

        // Payment form fields
        Text linkPriceLabel = new Text("Link Price: " + movie.getPrice());

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Enter your customer id");

        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Enter your 16-digit card number");

        Button payButton = new Button("Buy Link");
        payButton.getStyleClass().add("pay-button");

        // Set action on pay button
        payButton.setOnAction(e -> {
            // Collect payment details
            String email = emailField.getText();
            String name = nameField.getText();
            String cardNumber = cardNumberField.getText();
            String customerId = customerIdField.getText();

            if (email.isEmpty() || name.isEmpty() || cardNumber.isEmpty()|| customerId.isEmpty())  {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
                return;
            }

            if (!cardNumber.matches("\\d{16}")) {  // Verify that the card number is exactly 16 digits
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Card number must be exactly 16 digits.");
                return;
            }

            // Extract the last 4 digits of the card number
            String lastFourDigits = cardNumber.substring(12);

            // Create a PurchaseLink object
            PurchaseLink purchaseLink = new PurchaseLink(
                    name,
                    LocalDateTime.now(),  // Current purchase time
                    Integer.parseInt(customerId),  // Customer ID (replace with actual ID if necessary)
                    Integer.parseInt(lastFourDigits),  // Last 4 digits of card
                    movie.getTitle(),  // Movie title
                    email,
                    movie.getPrice()  // Price of the link
            );

            // Send the PurchaseLink object to the server
            sendPurchaseLinkToServer(purchaseLink);

            // Close the payment window
            paymentStage.close();
            mainWindowRoot.setEffect(null);  // הסרת הטשטוש לאחר סגירת החלון
        });

        paymentStage.setOnCloseRequest(event -> mainWindowRoot.setEffect(null)); // הסרת הטשטוש בעת סגירת החלון

        paymentDetails.getChildren().addAll(linkPriceLabel, nameField, emailField, customerIdField, cardNumberField, payButton);


        Scene paymentScene = new Scene(paymentDetails, 400, 230);
        paymentStage.setScene(paymentScene);
        paymentStage.show();
    }

    @FXML
    public void toggleSearchWindow() {
        boolean isCurrentlyVisible = searchWindow.isVisible();
        searchWindow.setVisible(!isCurrentlyVisible);
        searchWindow.setManaged(!isCurrentlyVisible);  // Ensure layout management is updated

        if (!isCurrentlyVisible) {
            searchWindow.getStyleClass().remove("hidden");
            searchWindow.getStyleClass().add("visible");  // Show the search window with a fade-in effect
            searchWindow.toFront();  // Bring the search window to the front
        } else {
            searchWindow.getStyleClass().remove("visible");
            searchWindow.getStyleClass().add("hidden");  // Hide the search window with a fade-out effect
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static void showPurchaseLinkReceipt(String message){
        Platform.runLater(() -> {

            // Show the receipt in an alert dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Purchase Link Receipt");
            alert.setHeaderText("Purchase Link Receipt");  // Optional: remove header text
            alert.setContentText(message);
            alert.showAndWait();  // Use showAndWait to block until the user closes the alert
        });
    }

}
