package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MoviesList {
    @FXML
    private ListView<String> ListOfMovies;
    public static List<Movie> list_allMovies = new ArrayList<>();

    private Movie movie=null;
    @FXML
    private TextField time_entered;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    void updateTiming(ActionEvent event) throws IOException {
        if (this.movie == null){
            showCompletionMessage("Error","Please select a movie to update");
        }
        else{
            if(time_entered.getText().isEmpty()){
                showCompletionMessage("Error","Please enter time to update");
            }
            else{
                LocalTime newshowtime = parseTime(time_entered.getText());
                if(newshowtime==null){
                    showCompletionMessage("Error", "Invalid time format, please enter time in HH:mm format");
                    time_entered.clear();
                }
                else{
                    SimpleClient.getClient().sendToServer("Update time @"+time_entered.getText()+"@"+movie.getTitle());
                }
      }
}

    }
    private LocalTime parseTime(String timeString) {
        try {
            // Check if the string is a valid time
            LocalTime time = LocalTime.parse(timeString, TIME_FORMATTER);
            return time;
        } catch (DateTimeParseException e) {
            // Handle the exception if the time is not valid
            return null;
        }
    }
    private void showCompletionMessage(String title, String message) {
        // Display an alert dialog to the user
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Use INFORMATION type for completion message
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    public void initialize() {

        if (list_allMovies.isEmpty()) {
            Platform.runLater(() -> {
                showCompletionMessage("Empty", "There are no movies available.");
                try {
                    App.setRoot("MoviesTable"); // Assuming "main" is the main scene you want to switch to
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return;
        }

        // Add movies to the list view
        for (Movie movie : list_allMovies) {
            ListOfMovies.getItems().add("Movie ID: " + movie.getId() + " - " + movie.getTitle() + " - " + movie.getDescription() + " - " + movie.getReleaseDate());
        }

        // Set the event handler for mouse clicks
        ListOfMovies.setOnMouseClicked(event -> {
            String selectedMovie = ListOfMovies.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                // Split the selected item based on the delimiter " - "
                String[] parts = selectedMovie.split(" - ");
                if (parts.length > 0) {
                    // Extract the movie ID from the first part of the split string
                    String movieIdString = parts[0].trim();
                    // Remove the "Movie ID: " prefix to get the actual movie ID
                    String movieId = movieIdString.substring("Movie ID: ".length());
                    // Parse the movie ID string into an integer
                    int number = Integer.parseInt(movieId);

                    // Iterate through the list of movies
                    for (Movie movie : list_allMovies) {
                        // Check if the movie ID matches the selected movie ID
                        if (movie.getId() == number) {
                            // Found the selected movie, assign it to the movie variable and break the loop
                            this.movie = movie;
                            break;
                        }
                    }
                }
            }
        });
    }



}
