package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

    @FXML
    public void initialize() {
        genreComboBox.getItems().addAll("ALL", "Action", "Drama", "Comedy", "Horror", "Documentary");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchMovies());

        genreComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            searchMovies(); // קוראים לפונקציית החיפוש כאשר בוחרים ז'אנר
        });

        showAllOnlineMovies();

        // Ensure search window is hidden initially
        searchWindow.setVisible(false);
        searchWindow.setManaged(false);
    }

    @FXML
    public void showAllOnlineMovies() {
        SimpleClient client = SimpleClient.getClient();
        client.requestMoviesByOnlineStatus(true);
    }

    @FXML
    public void searchMovies() {
        String selectedGenre = genreComboBox.getValue();
        String movieTitle = searchField.getText();

        // נתייחס לערך של "ALL" כמו ז'אנר ריק
        if ("ALL".equals(selectedGenre)) {
            selectedGenre = null;
        }

        SimpleClient client = SimpleClient.getClient();
        System.out.println("Searching movies with criteria: Genre = " + selectedGenre + ", Title = " + movieTitle);
        client.requestOnlineMoviesByCriteria(selectedGenre, movieTitle);
    }

    public void displayMovies(List<Movie> movies) {
        movieTilePane.getChildren().clear();

        // הוספת לוג לבדיקת גודל הרשימה
        System.out.println("Number of movies received: " + (movies != null ? movies.size() : "null list"));

        if (movies == null || movies.isEmpty()) {
            Text noMoviesText = new Text("No movies found.");
            movieTilePane.getChildren().add(noMoviesText);
        } else {
            for (Movie movie : movies) {
                VBox movieBox = new VBox(5);
                ImageView movieImage = new ImageView(new Image(movie.getImagePath()));
                movieImage.setFitHeight(150);
                movieImage.setFitWidth(100);
                Text movieTitle = new Text(movie.getTitle());
                movieBox.getChildren().addAll(movieImage, movieTitle);
                movieTilePane.getChildren().add(movieBox);
            }
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
