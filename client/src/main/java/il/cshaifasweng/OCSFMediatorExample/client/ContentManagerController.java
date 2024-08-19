package il.cshaifasweng.OCSFMediatorExample.client;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ContentManagerController {

    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> titleColumn;
    @FXML private TableColumn<Movie, String> placeColumn;
    @FXML private TableColumn<Movie, String> showtimeColumn;
    private ObservableList<Movie> movieList;

    @FXML private TextField titleField;
    @FXML private DatePicker showtimeDatePicker;
    @FXML private TextField showtimeHourField;
    @FXML private TextField showtimeMinuteField;
    @FXML private DatePicker releaseDatePicker;
    @FXML private TextField directorField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private TextField ratingField;
    @FXML private CheckBox isOnlineCheckbox;
    @FXML private TextField genreField;
    @FXML private TextField imagePathField;

    private SimpleClient client;

    @FXML
    private Label movieCountLabel;
    @FXML
    private void initialize() {
        try {
            titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
            placeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlace()));
            showtimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShowtime().toString()));
            loadMoviesIntoTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = SimpleClient.getClient();
        client.setContentManagerController(this);
        updateMovieCount();
    }

    private void loadMoviesIntoTable() {
        try {
            SimpleClient.getClient().sendToServer("get all movies"); // Request all movies from the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateMovieTable(List<Movie> movies) {
        movieList = FXCollections.observableArrayList(movies);
        movieTable.setItems(movieList);
    }
    public void updateMovieCount() {
        try {
            client.sendToServer("#getMovieCount");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMovieCount(int count) {
        Platform.runLater(() -> {
            movieCountLabel.setText("Movies in Database: " + count);
        });
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Movie Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleAddMovie() {
        try {
            String title = titleField.getText();
            LocalDateTime showtime = getShowtime();
            LocalDate releaseDate = releaseDatePicker.getValue();
            String director = directorField.getText();
            String description = descriptionArea.getText();
            int duration = Integer.parseInt(durationField.getText());
            float rating = Float.parseFloat(ratingField.getText());
            float price = Float.parseFloat(priceField.getText());
            boolean isOnline = isOnlineCheckbox.isSelected();
            String genre = genreField.getText();
            byte[] imageData = null;
            File imageFile = new File(imagePathField.getText());

            if (imageFile.exists()) {
                try {
                    imageData = Files.readAllBytes(imageFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error reading the image file.");
                }
            } else {
                System.err.println("Image file does not exist at path: " + imagePathField.getText());
            }

            if (imageData != null) {
                Movie movie = new Movie(title, showtime, releaseDate, director, description, price, isOnline, genre, duration, rating, imageData);
                SimpleClient.getClient().sendToServer(movie.toString());
                clearForm();
            } else {
                System.err.println("Failed to load image data. Movie not sent.");
            }

            // Show a confirmation message
            showAlert(Alert.AlertType.INFORMATION, "Movie Added", "The movie has been successfully added.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add movie: " + e.getMessage());
        }
    }

    private LocalDateTime getShowtime() {
        LocalDate date = showtimeDatePicker.getValue();
        int hour = Integer.parseInt(showtimeHourField.getText());
        int minute = Integer.parseInt(showtimeMinuteField.getText());
        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }
    public void handleDeleteMovie(ActionEvent actionEvent) {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            try {
                SimpleClient.getClient().sendToServer("delete movie " + selectedMovie.getId()); // Send delete request
                movieList.remove(selectedMovie);// Remove from list locally
                showAlert(Alert.AlertType.CONFIRMATION, "Confirmation", "Movie deleted successfully");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error" ,"Failed to delete movie" + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "NO MOVIE SELECTED" ,"Please select a movie");
        }
    }


    private void clearForm() {
        titleField.clear();
        showtimeDatePicker.setValue(null);
        showtimeHourField.clear();
        showtimeMinuteField.clear();
        releaseDatePicker.setValue(null);
        directorField.clear();
        descriptionArea.clear();
        priceField.clear();
        isOnlineCheckbox.setSelected(false);
        genreField.clear();
        imagePathField.clear();
        ratingField.clear();
        durationField.clear();
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleBack() {
        try {
            App.goBack();  // חזרה לחלון הקודם
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error" ,"Failed to load the previous screen." + e.getMessage());
        }
    }
    @FXML
    public void handleGoTo(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        System.out.println(actionEvent.toString());
        String fxmlFile = "";

        switch (clickedButton.getText()) {
            case "Add Movie":
                fxmlFile = "content_manager_addmovie";
                break;
            case "Delete Movie":
                fxmlFile = "content_manager_deletemovie";
                break;
            case "Update Price":
                fxmlFile = "UpdatePrice.fxml";
                break;
            case "Update Showtime":
                fxmlFile = "UpdateShowtime.fxml";
                break;
        }
        try {
            App.setRoot(fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}