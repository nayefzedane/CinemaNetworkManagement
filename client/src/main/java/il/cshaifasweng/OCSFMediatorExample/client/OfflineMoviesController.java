package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.effect.GaussianBlur;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;

public class OfflineMoviesController {

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
        buyButton.setOnAction(e -> openPaymentWindow(movie));  // פתיחת חלון התשלום

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
                buyButton // הוספת כפתור BUY
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
        // יצירת Stage חדש עבור חלון התשלום
        Stage paymentStage = new Stage();
        paymentStage.setTitle("Payment Window");

        // יצירת VBox לפריסת הפריטים
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        // הוספת פרטים על הסרט
        Label movieLabel = new Label("Movie: " + movie.getTitle());
        Label cinemaLabel = new Label("Cinema: " + movie.getPlace());
        Label priceLabel = new Label("Price: $" + movie.getPrice());

        // הוספת שדה להזנת מספר המושב
        Label seatLabel = new Label("Seat Number:");
        TextField seatField = new TextField();

        // כפתור לתשלום
        Button payButton = new Button("Pay");

        // פעולה שמתרחשת כאשר לוחצים על כפתור התשלום
        payButton.setOnAction(event -> {
            String seatNumber = seatField.getText();
            if (!seatNumber.isEmpty()) {
                System.out.println("Payment successful for seat: " + seatNumber);
                paymentStage.close(); // סגירת החלון לאחר תשלום מוצלח
            } else {
                System.out.println("Please enter a seat number.");
            }
        });

        // כפתור לסגירת החלון
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> paymentStage.close());

        // הוספת כל הפריטים ל-VBox
        vbox.getChildren().addAll(movieLabel, cinemaLabel, priceLabel, seatLabel, seatField, payButton, closeButton);

        // יצירת סצנה והצגת החלון בגודל גדול יותר
        Scene paymentScene = new Scene(vbox, 600, 400); // גודל החלון 600x400 פיקסלים
        paymentStage.setScene(paymentScene);
        paymentStage.show();
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
}
