package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;

import java.io.ByteArrayInputStream;
import java.util.List;

public class OnlineMoviesController {

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

        // Log the number of movies received for debugging purposes
        System.out.println("Number of movies received: " + (movies != null ? movies.size() : "null list"));

        if (movies == null || movies.isEmpty()) {
            Text noMoviesText = new Text("No movies found.");
            movieTilePane.getChildren().add(noMoviesText);  // Display a "No movies found" message
        } else {
            for (Movie movie : movies) {

                VBox movieBox = new VBox(5);
                ImageView movieImage = new ImageView(new Image(movie.getImagePath()));
                if (movie.getImageData() != null && movie.getImageData().length > 0) {
                    // Convert byte array to Image
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(movie.getImageData());
                    movieImage = new ImageView(new Image(inputStream));
                }

                movieImage.setFitHeight(150);
                movieImage.setFitWidth(100);
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
        // Create a new window for displaying movie details
        VBox movieDetailsBox = new VBox(20);
        movieDetailsBox.getStyleClass().add("movie-details");  // Add a custom style class for movie details

        // Close button for the details window
        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> {
            detailsStage.close();  // Close the details window
            movieTilePane.setEffect(null);  // Remove the blur effect from the main window
        });

        HBox titleBar = new HBox(closeButton);
        titleBar.setAlignment(Pos.TOP_RIGHT);  // Align the close button to the top right

        // Movie details: image, title, and description
        ImageView movieImage = new ImageView(new Image(movie.getImagePath()));
        movieImage.setFitHeight(300);
        movieImage.setFitWidth(400);

        Text movieTitle = new Text(movie.getTitle());
        Text movieDescription = new Text("Description: " + movie.getDescription());

        VBox movieInfo = new VBox(10, movieTitle, movieDescription);  // Container for movie info

        movieDetailsBox.getChildren().addAll(titleBar, movieImage, movieInfo);  // Add components to the details box

        // Set up the details window with a ScrollPane for scrolling if needed
        ScrollPane scrollPane = new ScrollPane(movieDetailsBox);
        detailsStage = new Stage();
        Scene scene = new Scene(scrollPane, 700, 600);
        detailsStage.setScene(scene);
        detailsStage.initStyle(StageStyle.UNDECORATED);  // Remove window decorations (e.g., title bar)
        detailsStage.setResizable(false);  // Disable resizing of the details window

        // Apply a blur effect to the main window while the details window is open
        movieTilePane.setEffect(blurEffect);

        // Display the details window
        detailsStage.show();
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
}
