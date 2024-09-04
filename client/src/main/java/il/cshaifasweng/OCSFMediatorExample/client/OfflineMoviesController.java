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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    private List<Movie> upcomingMovies;
    private int currentUpcomingIndex = 0;

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
                movieImage.setFitHeight(250);
                movieImage.setFitWidth(150);
                movieImage.getStyleClass().add("movie-image");

                Button viewMoreButton = new Button("View More");
                viewMoreButton.getStyleClass().add("view-more");

                // להוסיף את הכפתור "View More" לתוך StackPane כדי להניח אותו מעל התמונה
                imageContainer.getChildren().addAll(movieImage, viewMoreButton);

                Text movieTitle = new Text(movie.getTitle());
                movieTitle.getStyleClass().add("movie-title");

                movieBox.getChildren().addAll(imageContainer, movieTitle);
                movieTilePane.getChildren().add(movieBox);

                // הוספת EventHandler לכפתור "View More"
                viewMoreButton.setOnAction(event -> {
                    // תוסיף כאן את הקוד לפתיחת חלון פרטים נוספים
                    System.out.println("View more clicked for movie: " + movie.getTitle());
                });
            }
        }
    }

    public void setUpcomingMovies(List<Movie> movies) {
        this.upcomingMovies = movies.stream()
                .filter(movie -> movie.getShowtime().toLocalDate().isAfter(LocalDate.now()))
                .sorted((m1, m2) -> m1.getShowtime().compareTo(m2.getShowtime()))
                .limit(4) // Display max 4 upcoming movies
                .collect(Collectors.toList());

        System.out.println("Filtered " + upcomingMovies.size() + " upcoming movies.");

        currentUpcomingIndex = 0;
        updateUpcomingMovieDisplay();
    }

    @FXML
    private void showNextUpcomingMovie() {
        if (upcomingMovies != null && !upcomingMovies.isEmpty()) {
            currentUpcomingIndex = (currentUpcomingIndex + 1) % upcomingMovies.size();
            System.out.println("Showing next upcoming movie: Index " + currentUpcomingIndex);
            updateUpcomingMovieDisplay();
        }
    }

    @FXML
    private void showPreviousUpcomingMovie() {
        if (upcomingMovies != null && !upcomingMovies.isEmpty()) {
            currentUpcomingIndex = (currentUpcomingIndex - 1 + upcomingMovies.size()) % upcomingMovies.size();
            System.out.println("Showing previous upcoming movie: Index " + currentUpcomingIndex);
            updateUpcomingMovieDisplay();
        }
    }

    private void updateUpcomingMovieDisplay() {
        if (upcomingMovies != null && !upcomingMovies.isEmpty()) {
            Movie movie = upcomingMovies.get(currentUpcomingIndex);
            System.out.println("Updating display for movie: " + movie.getTitle() + " at index " + currentUpcomingIndex);
            upcomingMovieImage.setImage(new Image(movie.getImagePath()));
            upcomingMovieTitle.setText(movie.getTitle());
            upcomingMovieDate.setText(movie.getShowtime().toString());
            upcomingMovieDescription.setText(movie.getDescription());

            upcomingMovieImage.setVisible(true);
            upcomingMovieTitle.setVisible(true);
            upcomingMovieDate.setVisible(true);
            upcomingMovieDescription.setVisible(true);
            prevButton.setVisible(true);
            nextButton.setVisible(true);

            // בודקים אם הורה של ImageView הוא StackPane לפני שמנסים להוסיף את הכפתור
            if (upcomingMovieImage.getParent() instanceof StackPane) {
                StackPane imageContainer = (StackPane) upcomingMovieImage.getParent();
                if (imageContainer.getChildren().size() == 1) {
                    Button viewMoreButton = new Button("View More");
                    viewMoreButton.getStyleClass().add("upcoming-view-more");
                    imageContainer.getChildren().add(viewMoreButton);

                    viewMoreButton.setOnAction(event -> {
                        // תוסיף כאן את הקוד לפתיחת חלון פרטים נוספים לסרטים שיוצגו בקרוב
                        System.out.println("View more clicked for upcoming movie: " + movie.getTitle());
                    });
                }
            } else {
                System.out.println("The parent of upcomingMovieImage is not a StackPane.");
            }
        } else {
            System.out.println("No upcoming movies to display.");
            upcomingMovieImage.setVisible(false);
            upcomingMovieTitle.setVisible(false);
            upcomingMovieDate.setVisible(false);
            upcomingMovieDescription.setVisible(false);
            prevButton.setVisible(false);
            nextButton.setVisible(false);
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
