package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

public class OfflineMoviesController {

    @FXML
    private ListView<String> movieListView;

    @FXML
    private ComboBox<String> cinemaComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> genreComboBox;

    @FXML
    private TextField searchField; // שדה חיפוש לפי שם הסרט

    @FXML
    public void initialize() {
        cinemaComboBox.getItems().addAll("ALL", "Cinema City", "Yes Planet", "Rav Chen", "Lev Cinema");
        genreComboBox.getItems().addAll("ALL", "Action", "Drama", "Comedy", "Sci-Fi", "Horror"); // דוגמה לז'אנרים

        // האזנה לשינויים בטקסט של שדה החיפוש
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchMovies();
        });

        // האזנה לשינויים בתאריך התחלה או סיום
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> searchMovies());
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> searchMovies());

        // האזנה לשינוי בקולנוע או בז'אנר
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

        // טעינת הסרטים בברירת מחדל כאשר הממשק נטען
        showAllMovies();
    }

    @FXML
    public void resetDates() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        showAllMovies(); // חזרה להצגת כל הסרטים הלא-אונליין
    }

    @FXML
    public void showAllMovies() {
        SimpleClient client = SimpleClient.getClient();
        client.requestMoviesByOnlineStatus(false);  // מבקש את כל הסרטים הלא-אונליין מהשרת
    }

    @FXML
    public void searchMovies() {
        String selectedCinema = cinemaComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String selectedGenre = genreComboBox.getValue();
        String movieTitle = searchField.getText(); // קבלת ערך משדה החיפוש

        if ("ALL".equals(selectedCinema)) {
            selectedCinema = null; // אם נבחרה האפשרות "ALL", נאפס את הבחירה
        }
        if ("ALL".equals(selectedGenre)) {
            selectedGenre = null; // אם נבחרה האפשרות "ALL", נאפס את הבחירה
        }

        // קריאה לפונקציה עם הפרמטר isOnline=false כדי לוודא חיפוש רק על סרטים לא אונליין
        SimpleClient client = SimpleClient.getClient();
        client.requestMoviesByAdvancedCriteria(selectedCinema, startDate, endDate, selectedGenre, movieTitle, false);
    }

    public void displayMovies(List<Movie> movies) {
        movieListView.getItems().clear();
        for (Movie movie : movies) {
            movieListView.getItems().add(movie.getTitle());
        }
    }
}
