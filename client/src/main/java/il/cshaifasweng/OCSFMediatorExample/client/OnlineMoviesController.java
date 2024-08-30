package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;

import java.util.List;

public class OnlineMoviesController {

    @FXML
    private TilePane movieTilePane;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        genreComboBox.getItems().addAll("ALL", "Action", "Drama", "Comedy", "Horror", "Documentary");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchMovies();
        });

        genreComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("ALL".equals(newValue)) {
                genreComboBox.setValue(null);
            }
            searchMovies();
        });

        showAllOnlineMovies();
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

        if ("ALL".equals(selectedGenre)) {
            selectedGenre = null;
        }

        SimpleClient client = SimpleClient.getClient();
        client.requestOnlineMoviesByCriteria(selectedGenre, movieTitle);
    }

    public void displayMovies(List<Movie> movies) {
        movieTilePane.getChildren().clear();
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
