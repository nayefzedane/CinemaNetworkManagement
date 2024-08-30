package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class OnlineMoviesController {

    @FXML
    private ListView<String> movieListView;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField searchField; // שדה חיפוש לפי שם הסרט

    @FXML
    public void initialize() {
        genreComboBox.getItems().addAll("ALL", "Action", "Drama", "Comedy", "Horror", "Documentary");

        // האזנה לשינויים בטקסט של שדה החיפוש
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchMovies();
        });

        // האזנה לשינוי בז'אנר
        genreComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("ALL".equals(newValue)) {
                genreComboBox.setValue(null);
            }
            searchMovies();
        });

        // טעינת הסרטים האונליין בברירת מחדל
        showAllOnlineMovies();
    }

    @FXML
    public void showAllOnlineMovies() {
        SimpleClient client = SimpleClient.getClient();
        client.requestMoviesByOnlineStatus(true);  // מבקש את כל הסרטים האונליין מהשרת
    }

    @FXML
    public void searchMovies() {
        String selectedGenre = genreComboBox.getValue();
        String movieTitle = searchField.getText(); // קבלת ערך משדה החיפוש

        if ("ALL".equals(selectedGenre)) {
            selectedGenre = null; // אם נבחרה האפשרות "ALL", נאפס את הבחירה
        }

        SimpleClient client = SimpleClient.getClient();
        client.requestOnlineMoviesByCriteria(selectedGenre, movieTitle); // שליחת הבקשה עם כל הקריטריונים
    }

    public void displayMovies(List<Movie> movies) {
        movieListView.getItems().clear();
        for (Movie movie : movies) {
            movieListView.getItems().add(movie.getTitle());
        }
    }
}
