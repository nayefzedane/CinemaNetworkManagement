package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.effect.GaussianBlur;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;

public class OfflineMoviesController {
    String currentScene = " "; // this variable will save if we are on buy with cash or buy with package

    @FXML
    private TilePane movieTilePane;

    @FXML
    private ComboBox<String> cinemaComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private ImageView upcomingMovieImage;

    @FXML
    private Text upcomingMovieTitle;

    @FXML
    private Text upcomingMovieDate;

    @FXML
    private Text upcomingMovieDescription;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private VBox searchWindow;

    @FXML
    private StackPane mainWindowRoot;

    private List<Movie> upcomingMovies;
    private int currentUpcomingIndex = 0;
    private GaussianBlur blurEffect = new GaussianBlur(20); // טשטוש קבוע
    private Stage detailsStage; // הגדרת stage כדי שניתן יהיה לסגור אותו מבחוץ

    @FXML
    public void initialize() {
        // Initialize combo boxes
        cinemaComboBox.getItems().addAll("ALL", "Cinema City", "Yes Planet", "Rav Chen", "Lev Cinema");
        genreComboBox.getItems().addAll("ALL", "Action", "Drama", "Comedy", "Sci-Fi", "Horror");

        // Add listeners for search criteria
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchMovies());
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> searchMovies());
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> searchMovies());
        cinemaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("ALL".equals(newValue)) {
                cinemaComboBox.setValue(null);
            }
            searchMovies();
        });
        genreComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("ALL".equals(newValue)) {
                genreComboBox.setValue(null);
            }
            searchMovies();
        });

        // Show all movies initially
        showAllMovies();

        // Ensure search window is hidden initially
        searchWindow.setVisible(false);
        searchWindow.setManaged(false);
    }

    @FXML
    public void resetDates() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        showAllMovies();
    }

    @FXML
    public void showAllMovies() {
        SimpleClient client = SimpleClient.getClient();
        System.out.println("Requesting all movies that are not online.");
        client.requestMoviesByOnlineStatus(false);
    }

    @FXML
    public void searchMovies() {
        String selectedCinema = cinemaComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String selectedGenre = genreComboBox.getValue();
        String movieTitle = searchField.getText();

        if ("ALL".equals(selectedCinema)) {
            selectedCinema = null;
        }
        if ("ALL".equals(selectedGenre)) {
            selectedGenre = null;
        }

        System.out.println("Searching movies with criteria: Cinema=" + selectedCinema + ", StartDate=" + startDate + ", EndDate=" + endDate + ", Genre=" + selectedGenre + ", Title=" + movieTitle);

        SimpleClient client = SimpleClient.getClient();
        client.requestMoviesByAdvancedCriteria(selectedCinema, startDate, endDate, selectedGenre, movieTitle, false);
    }

    public void displayMovies(List<Movie> movies) {
        movieTilePane.getChildren().clear();

        if (movies == null || movies.isEmpty()) {
            Text noMoviesText = new Text("No movies found.");
            movieTilePane.getChildren().add(noMoviesText);
        } else {
            for (Movie movie : movies) {
                VBox movieBox = new VBox(10);
                movieBox.getStyleClass().add("movie-box");

                StackPane imageContainer = new StackPane();
                ImageView movieImage = new ImageView(new Image(movie.getImagePath()));
                if (movie.getImageData() != null && movie.getImageData().length > 0) {
                    // Convert byte array to Image
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(movie.getImageData());
                    movieImage = new ImageView(new Image(inputStream));
                }
                movieImage.setFitHeight(250);
                movieImage.setFitWidth(150);
                movieImage.getStyleClass().add("movie-image");

                Button viewMoreButton = new Button("View More");
                viewMoreButton.getStyleClass().add("view-more");

                imageContainer.getChildren().addAll(movieImage, viewMoreButton);

                Text movieTitle = new Text(movie.getTitle());
                movieTitle.getStyleClass().add("movie-title");

                movieBox.getChildren().addAll(imageContainer, movieTitle);
                movieTilePane.getChildren().add(movieBox);

                viewMoreButton.setOnAction(event -> {
                    showMovieDetails(movie);
                });
            }
        }
    }

    private void showMovieDetails(Movie movie) {
        // יצירת חלון חדש עם VBox שמכיל את כל הפרטים של הסרט
        VBox movieDetailsBox = new VBox(20);
        movieDetailsBox.getStyleClass().add("movie-details");

        // יצירת כפתור סגירה
        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> {
            detailsStage.close(); // סוגר את החלון
            mainWindowRoot.setEffect(null); // הסרת הטשטוש
        });

        HBox titleBar = new HBox(closeButton);
        titleBar.setAlignment(Pos.TOP_RIGHT); // הצבת כפתור הסגירה בצד ימין למעלה

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
        movieImage.getStyleClass().add("movie-image");

        // פרטים נוספים של הסרט
        Text movieTitle = new Text(movie.getTitle());
        movieTitle.getStyleClass().add("movie-title");

        Text movieDescription = new Text("Description: " + movie.getDescription());
        movieDescription.getStyleClass().add("movie-description");
        movieDescription.setWrappingWidth(400);

        Text movieGenre = new Text("Genre: " + movie.getGenre());
        Text movieShowtime = new Text("Showtime: " + movie.getShowtime());
        Text movieReleaseDate = new Text("Release Date: " + movie.getReleaseDate());
        Text movieDuration = new Text("Duration: " + movie.getDuration() + " minutes");
        Text movieRating = new Text("Rating: " + movie.getRating());
        Text movieDirector = new Text("Director: " + movie.getDirector());
        Text moviePlace = new Text("Place: " + movie.getPlace());
        Text moviePrice = new Text("Price: $" + movie.getPrice());
        Text movieAvailableSeat = new Text("Available Seats: " + movie.getAvailableSeat());
        Text movieHallNumber = new Text("Hall Number: " + movie.getHallNumber());
        Text movieIsOnline = new Text("Available Online: " + (movie.isOnline() ? "Yes" : "No"));

        // כפתור "BUY"
        Button buyButton = new Button("BUY");
        buyButton.getStyleClass().add("buy-button");
        buyButton.setOnAction(e -> {
            detailsStage.close();  // Close the movie details window
            currentScene = "cash";

            openPaymentWindow(movie);  // Open the payment window
        });
        //buy with package:
        Button packageButton = new Button("I have Package Card");
        packageButton.getStyleClass().add("buy-button");
        packageButton.setOnAction(e -> {
            detailsStage.close();  // Close the movie details window
            currentScene = "package";

            openPaymentWindow(movie);  // Open the payment window
        });
        HBox buttonBox = new HBox(10, buyButton, packageButton);  // Add spacing of 10 between buttons
        buttonBox.setAlignment(Pos.CENTER_RIGHT);  // Align the buttons to the right




        // סידור הפרטים בתוך VBox
        VBox movieInfo = new VBox(10,
                movieTitle,
                movieDescription,
                movieGenre,
                movieShowtime,
                movieReleaseDate,
                movieDuration,
                movieRating,
                movieDirector,
                moviePlace,
                moviePrice,
                movieAvailableSeat,
                movieHallNumber,
                movieIsOnline,
                buttonBox  // Adding both buttons horizontally

        );
        movieInfo.getStyleClass().add("movie-info");

        // הוספת התמונה והפרטים ל-VBox
        movieDetailsBox.getChildren().addAll(titleBar, movieImage, movieInfo);

        // הוספת ScrollPane כדי לאפשר גלילה
        ScrollPane scrollPane = new ScrollPane(movieDetailsBox);
        scrollPane.setFitToWidth(true);  // התאמת התוכן לרוחב
        scrollPane.setPannable(true);    // לאפשר גרירה עם העכבר

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

        // סגירת החלון בלחיצה מחוץ לחלון
        mainWindowRoot.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (detailsStage.isShowing() && !detailsStage.isFocused()) {
                detailsStage.close();
                mainWindowRoot.setEffect(null); // הסרת הטשטוש
            }
        });

        // הגדרת החלון הדינאמי שיהיה קבוע ולא ניתן להזזה
        detailsStage.initStyle(StageStyle.UNDECORATED);
        detailsStage.setResizable(false); // לא ניתן לשנות גודל

        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void openPaymentWindow(Movie movie) {
        // Create a new Stage for the payment window
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Choose your seat");

        // Create a VBox to  the items
        VBox vbox = new VBox(20);  // More spacing between elements
        vbox.setPadding(new Insets(20));

        // Create a title label
        Label titleLabel = new Label("Please choose your seat");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");  // Styling the title

        // Create a GridPane to represent the seat map
        GridPane seatGrid = new GridPane();
        seatGrid.setPadding(new Insets(10));
        seatGrid.setHgap(10);
        seatGrid.setVgap(10);

        // Get the seat map from the movie object
        int[][] hallMap = movie.getHallMap();

        // Set row and column constraints to make buttons fill the space
        for (int row = 0; row < hallMap.length; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / hallMap.length); // Distribute row height equally
            seatGrid.getRowConstraints().add(rowConstraints);
        }

        for (int col = 0; col < hallMap[0].length; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / hallMap[0].length); // Distribute column width equally
            seatGrid.getColumnConstraints().add(colConstraints);
        }

        // Loop through the 2D array to create buttons for each seat
        for (int row = 0; row < hallMap.length; row++) {
            for (int col = 0; col < hallMap[row].length; col++) {
                String seatText = "Seat " + (row + 1) + "-" + (col + 1);
                Button seatButton = new Button(seatText);

                // Set the button size to fill its grid cell
                seatButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                // Set the color of the button based on seat availability
                if (hallMap[row][col] == 1) {
                    seatButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;"); // Better red with white text
                    seatButton.setDisable(true); // Disable taken seats
                } else {
                    seatButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;"); // Green for available
                }

                // Handle seat selection
                seatButton.setOnAction(event -> {
                    String selectedSeat = seatText;
                    System.out.println("Selected Seat: " + selectedSeat);

                    // Open a new screen for user details (to be implemented later)
                    if(currentScene.equals("cash")){
                        openUserDetailsWindow(selectedSeat, movie);
                    }
                    if(currentScene.equals("package")){
                        openPackageDetailsWindow(selectedSeat, movie);
                    }

                    paymentStage.close();  // Close the seat selection window
                });

                // Add the seat button to the grid
                seatGrid.add(seatButton, col, row);
            }
        }

        // Add the title and grid to the VBox
        vbox.getChildren().addAll(titleLabel, seatGrid);
        // הסרת הטשטוש כשסוגרים את החלון
        paymentStage.setOnCloseRequest(event -> mainWindowRoot.setEffect(null));

        // סגירת החלון בלחיצה מחוץ לחלון
        mainWindowRoot.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (paymentStage.isShowing() && !paymentStage.isFocused()) {
                paymentStage.close();
                mainWindowRoot.setEffect(null); // הסרת הטשטוש
            }
        });

        // Create a scene and show the window with larger size
        Scene paymentScene = new Scene(vbox, 800, 600); // Increased window size
        paymentStage.setScene(paymentScene);
        paymentStage.show();
    }





    private void openUserDetailsWindow(String seatNumber, Movie movie) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Enter your details");

        // Create input fields for user details
        Label seatLabel = new Label("You selected: " + seatNumber);
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label customerIdLabel = new Label("Customer ID:");
        TextField customerIdField = new TextField();

        Label customerEmailLabel = new Label("Email:");
        TextField customerEmailField = new TextField();

        Label cardNumberLabel = new Label("Card Number (16 digits):");
        TextField fullCardNumberField = new TextField();

        // Label for movie price (from the selected movie)
        Label priceLabel = new Label("Price: $" + movie.getPrice());

        // Create a button to handle ticket purchase
        Button buyTicketButton = new Button("Buy Ticket");

        // Action when the button is clicked
        buyTicketButton.setOnAction(event -> {
            // Retrieve data from fields
            String name = nameField.getText();
            String customerIdStr = customerIdField.getText();
            String customerEmail = customerEmailField.getText();
            String fullCardNumber = fullCardNumberField.getText();

            // Validate the input (example: check if fields are empty or if the card number is valid)
            if (name.isEmpty() || customerIdStr.isEmpty() || customerEmail.isEmpty() || fullCardNumber.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
                return;
            }

            if (!fullCardNumber.matches("\\d{16}")) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Card number must be 16 digits.");
                return;
            }

            int customerId;
            try {
                customerId = Integer.parseInt(customerIdStr);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Customer ID must be a valid number.");
                return;
            }

            // Extract the last 4 digits from the full card number
            String lastFourDigits = fullCardNumber.substring(fullCardNumber.length() - 4);

            // Set the purchase date as the current date
            LocalDate purchaseDate = LocalDate.now();

            // Use the movie's price for this ticket
            float price = movie.getPrice();
            //public purchaseCard(LocalDate purchaseDate, String branchName, float price, int customerId,
            //                    int paymentCardLastFour, String movieTitle, LocalDateTime showTime, String costMail)
            // Create a new Ticket entity (assuming you have a Ticket class)
            purchaseCard ticket = new purchaseCard(purchaseDate, movie.getPlace(), price, customerId, Integer.parseInt(lastFourDigits),movie.getTitle() ,movie.getShowtime(),customerEmail,name, seatNumber, movie.getId());

            // Send the Ticket to the server
            try {
                SimpleClient.getClient().sendToServer(ticket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Close the window after successful purchase
            detailsStage.close();
            mainWindowRoot.setEffect(null);
        });

        // Layout for the form
        VBox vbox = new VBox(10, seatLabel, nameLabel, nameField, customerIdLabel, customerIdField, customerEmailLabel, customerEmailField,
                cardNumberLabel, fullCardNumberField, priceLabel, buyTicketButton);
        vbox.setPadding(new Insets(20));
        // הסרת הטשטוש כשסוגרים את החלון
        detailsStage.setOnCloseRequest(event -> mainWindowRoot.setEffect(null));

        // סגירת החלון בלחיצה מחוץ לחלון
        mainWindowRoot.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (detailsStage.isShowing() && !detailsStage.isFocused()) {
                detailsStage.close();
                mainWindowRoot.setEffect(null); // הסרת הטשטוש
            }
        });

        // Create the scene and show the details window
        Scene detailsScene = new Scene(vbox, 400, 400);
        detailsStage.setScene(detailsScene);
        detailsStage.show();
    }
    //function after the user clicked enter with package button:
    private void openPackageDetailsWindow(String seatNumber, Movie movie) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Enter your Package Details");

        // Create input fields for user details
        Label seatLabel = new Label("You selected: " + seatNumber);
        Label customerIdLabel = new Label("Customer ID:");
        TextField customerIdField = new TextField();

        Label packageIdLabel = new Label("Package Card ID:");
        TextField packageIdField = new TextField();

        // Create a button to submit package details
        Button submitButton = new Button("Submit");

        // Action when the button is clicked
        submitButton.setOnAction(event -> {
            // Retrieve data from fields
            String customerIdStr = customerIdField.getText();
            String packageIdStr = packageIdField.getText();

            // Validate the input (example: check if fields are empty)
            if (customerIdStr.isEmpty() || packageIdStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
                return;
            }

            int customerId;
            try {
                customerId = Integer.parseInt(customerIdStr);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Customer ID must be a valid number.");
                return;
            }

            // Send the package details to the server
            try {
                SimpleClient.getClient().sendToServer("PackageCardRequest:" + customerId + ":" + packageIdStr + ":" + seatNumber + ":" + movie.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Close the window after sending the request
            detailsStage.close();
            mainWindowRoot.setEffect(null);
        });

        // Layout for the form
        VBox vbox = new VBox(10, seatLabel, customerIdLabel, customerIdField, packageIdLabel, packageIdField, submitButton);
        vbox.setPadding(new Insets(20));
        // סגירת החלון בלחיצה מחוץ לחלון
        // Use setOnCloseRequest and setOnHidden to ensure blur is removed
        detailsStage.setOnCloseRequest(event -> mainWindowRoot.setEffect(null));
        detailsStage.setOnHidden(event -> mainWindowRoot.setEffect(null));

        // Create the scene and show the details window
        Scene detailsScene = new Scene(vbox, 400, 300);
        detailsStage.setScene(detailsScene);
        detailsStage.show();
    }







    public void setUpcomingMovies(List<Movie> movies) {
        this.upcomingMovies = movies.stream()
                .filter(movie -> movie.getShowtime().toLocalDate().isAfter(LocalDate.now()))
                .sorted((m1, m2) -> m1.getShowtime().compareTo(m2.getShowtime()))
                .limit(4) // Display max 4 upcoming movies
                .collect(Collectors.toList());

        currentUpcomingIndex = 0;
        updateUpcomingMovieDisplay();
    }

    @FXML
    private void showNextUpcomingMovie() {
        if (upcomingMovies != null && !upcomingMovies.isEmpty()) {
            currentUpcomingIndex = (currentUpcomingIndex + 1) % upcomingMovies.size();
            updateUpcomingMovieDisplay();
        }
    }

    @FXML
    private void showPreviousUpcomingMovie() {
        if (upcomingMovies != null && !upcomingMovies.isEmpty()) {
            currentUpcomingIndex = (currentUpcomingIndex - 1 + upcomingMovies.size()) % upcomingMovies.size();
            updateUpcomingMovieDisplay();
        }
    }
    private void updateUpcomingMovieDisplay() {
        // בדיקה אם יש סרטים ברשימה
        if (upcomingMovies != null && !upcomingMovies.isEmpty()) {
            // שליפת הסרט הנוכחי מתוך הרשימה על פי האינדקס הנוכחי
            Movie movie = upcomingMovies.get(currentUpcomingIndex);

            // עדכון תמונת הסרט
            upcomingMovieImage.setImage(new Image(movie.getImagePath()));

            if (movie.getImageData() != null && movie.getImageData().length > 0) {
                // Convert byte array to Image
                ByteArrayInputStream inputStream = new ByteArrayInputStream(movie.getImageData());
                upcomingMovieImage.setImage(new Image(inputStream));
            }

            upcomingMovieTitle.setText(movie.getTitle());
            upcomingMovieDate.setText("Showtime: " + movie.getShowtime().toString()); // הוספת "Showtime"
            upcomingMovieDescription.setText(movie.getDescription());

            // ווידוא שהאלמנטים נראים
            upcomingMovieImage.setVisible(true);
            upcomingMovieTitle.setVisible(true);
            upcomingMovieDate.setVisible(true);
            upcomingMovieDescription.setVisible(true);

            // הפעלת כפתורי הניווט קדימה ואחורה
            prevButton.setVisible(true);
            nextButton.setVisible(true);

        } else {
            // אם אין סרטים ברשימה, הסתרת כל האלמנטים
            upcomingMovieImage.setVisible(false);
            upcomingMovieTitle.setVisible(false);
            upcomingMovieDate.setVisible(false);
            upcomingMovieDescription.setVisible(false);
            prevButton.setVisible(false);
            nextButton.setVisible(false);
        }
    }

    @FXML
    private void onUpcomingViewMoreClicked() {
        if (upcomingMovies != null && !upcomingMovies.isEmpty()) {
            Movie movie = upcomingMovies.get(currentUpcomingIndex);
            showMovieDetails(movie); // פתיחת חלון עם פרטי הסרט
        }
    }

    @FXML
    public void toggleSearchWindow() {
        boolean isCurrentlyVisible = searchWindow.isVisible();
        searchWindow.setVisible(!isCurrentlyVisible);
        searchWindow.setManaged(!isCurrentlyVisible);

        if (!isCurrentlyVisible) {
            searchWindow.getStyleClass().remove("hidden");
            searchWindow.getStyleClass().add("visible");
            searchWindow.toFront();
        } else {
            searchWindow.getStyleClass().remove("visible");
            searchWindow.getStyleClass().add("hidden");
        }
    }
    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void ShowReceipt(purchaseCard purchaseCard) {
        StringBuilder receiptMessage = new StringBuilder();
        receiptMessage.append("Receipt:\n");
        receiptMessage.append("=====================================\n");
        receiptMessage.append("Purchase ID: ").append(purchaseCard.getOrderId()).append("\n");  // Purchase ID
        receiptMessage.append("Name: ").append(purchaseCard.getName()).append("\n");
        receiptMessage.append("Customer ID: ").append(purchaseCard.getCustomerId()).append("\n");
        receiptMessage.append("Customer Email: ").append(purchaseCard.getCostMail()).append("\n");
        receiptMessage.append("Price: $").append(purchaseCard.getPrice()).append("\n");
        receiptMessage.append("Movie Title: ").append(purchaseCard.getMovieTitle()).append("\n");
        receiptMessage.append("Branch Name: ").append(purchaseCard.getBranchName()).append("\n");
        receiptMessage.append("Seat: ").append(purchaseCard.getSeat()).append("\n");
        receiptMessage.append("Showtime: ").append(purchaseCard.getShowTime()).append("\n");
        receiptMessage.append("Payment (Last 4 digits): ").append(purchaseCard.getPaymentCardLastFour()).append("\n");
        receiptMessage.append("Purchase Date: ").append(purchaseCard.getPurchaseDate()).append("\n");
        receiptMessage.append("=====================================\n");

        // Ensure the alert dialog is shown on the JavaFX Application Thread
        Platform.runLater(() -> {
            System.out.println(receiptMessage.toString());
            // Show the receipt in an alert dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ticket Purchase Receipt");
            alert.setHeaderText("Receipt");  // Optional: remove header text
            alert.setContentText(receiptMessage.toString());
            alert.showAndWait();  // Use showAndWait to block until the user closes the alert
        });
        SimpleClient client = SimpleClient.getClient();
        System.out.println("Requesting all movies that are not online.");
        client.requestMoviesByOnlineStatus(false);

    }
    public static void showPackageBuyReceipt(String message){
        Platform.runLater(() -> {
            System.out.println(message);
            // Show the receipt in an alert dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entering with package Receipt");
            alert.setHeaderText("Receipt");  // Optional: remove header text
            alert.setContentText(message);
            alert.showAndWait();  // Use showAndWait to block until the user closes the alert
        });
        SimpleClient client = SimpleClient.getClient();
        System.out.println("Requesting all movies that are not online.");
        client.requestMoviesByOnlineStatus(false);
    }

}
