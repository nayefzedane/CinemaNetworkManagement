package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

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
    public void initialize() {
        cinemaComboBox.getItems().addAll("ALL", "Cinema City", "Yes Planet", "Rav Chen", "Lev Cinema");
        genreComboBox.getItems().addAll("ALL", "Action", "Drama", "Comedy", "Sci-Fi", "Horror");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchMovies();
        });

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

        showAllMovies();
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

        SimpleClient client = SimpleClient.getClient();
        client.requestMoviesByAdvancedCriteria(selectedCinema, startDate, endDate, selectedGenre, movieTitle, false);
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
