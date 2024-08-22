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
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private TableColumn<Movie, String> priceColumn;
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
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
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

    public void handleUpdateShowtime(ActionEvent actionEvent) {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            // Validate the date
            LocalDate selectedDate = showtimeDatePicker.getValue();
            LocalDate today = LocalDate.now();
            if (selectedDate == null || !selectedDate.isAfter(today)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Date", "Please select a future date.");
                return;
            }

            // Validate the time fields
            String hourText = showtimeHourField.getText();
            String minuteText = showtimeMinuteField.getText();
            int hour, minute;

            try {
                hour = Integer.parseInt(hourText);
                minute = Integer.parseInt(minuteText);

                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Time", "Please enter a valid time (HH:MM).");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter numeric values for hours and minutes.");
                return;
            }

            try {
                SimpleClient.getClient().sendToServer("update showtime:" + selectedMovie.getId() + ":" + selectedDate.toString() +
                        ":" + hour + ":" + minute); // Send update showtime request
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update movie showtime: " + e.getMessage());
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Confirmation", "Movie showtime updated successfully \nNew show date and time are: " +
                    selectedDate.toString() + " , " + hour + ":" + minute);
            loadMoviesIntoTable();
        } else {
            showAlert(Alert.AlertType.ERROR, "No Movie Selected", "Please select a movie.");
        }
    }

    public void handleUpdatePrice(ActionEvent actionEvent) {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            try {
                double newPrice = Double.parseDouble(priceField.getText());
                if (newPrice >= 0) {
                    SimpleClient.getClient().sendToServer("send request:"+ selectedMovie.getTitle() + ":" + selectedMovie.getId() + ":" + selectedMovie.getPrice() + ":" + newPrice);
                    showAlert(Alert.AlertType.INFORMATION, "Confirmation", "Request to update movie price sent successfully \n new price: " + newPrice);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid price greater than 0.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the price.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update movie price: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "No Movie Selected", "Please select a movie to update.");
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
        System.out.println("Navigating to " + clickedButton.getText());
        String fxmlFile = "";

        switch (clickedButton.getText()) {
            case "Add Movie":
                fxmlFile = "content_manager_addmovie";
                break;
            case "Delete Movie":
                fxmlFile = "content_manager_deletemovie";
                break;
            case "Update Price":
                fxmlFile = "content_manager_updateprice";
                break;
            case "Update Showtime":
                fxmlFile = "content_manager_updateshowtime";
                break;
        }
        try {
            App.setRoot(fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}