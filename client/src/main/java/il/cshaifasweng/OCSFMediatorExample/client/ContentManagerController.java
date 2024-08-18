package il.cshaifasweng.OCSFMediatorExample.client;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
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

public class ContentManagerController {
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
        client = SimpleClient.getClient();
        client.setContentManagerController(this);
        updateMovieCount();
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

    // This method should be called whenever you return to this view
    public void refreshView() {
        initialize();
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
                System.out.println(movie);
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
    public void handleGoToAddMovie(ActionEvent actionEvent) {
        try {
            App.setRoot("content_manager_addmovie");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleGoToDeleteMovie(ActionEvent actionEvent) {
    }
    @FXML
    public void handleGoToUpdatePrice(ActionEvent actionEvent) {
    }
    @FXML
    public void handleGoToUpdateShowtime(ActionEvent actionEvent) {
    }
}