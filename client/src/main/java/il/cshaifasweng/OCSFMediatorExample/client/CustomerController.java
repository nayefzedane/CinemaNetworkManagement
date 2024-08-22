package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class CustomerController {

    @FXML
    private VBox moviesContainer;

    @FXML
    private void initialize() {
        // שולח בקשה לשרת לקבלת רשימת הסרטים
        try {
            SimpleClient.getClient().sendToServer("get all movies");
        } catch (IOException e) {
            showAlert("Error", "Failed to fetch movies from the server.");
        }
    }

    public void displayMovies(List<Movie> movies) {
        for (Movie movie : movies) {
            HBox movieBox = new HBox(10);
            ImageView imageView;

            // Check if the movie has image data
            if (movie.getImageData() != null && movie.getImageData().length > 0) {
                // Convert byte array to Image
                ByteArrayInputStream inputStream = new ByteArrayInputStream(movie.getImageData());
                imageView = new ImageView(new Image(inputStream));
            } else if (movie.getImagePath() != null && !movie.getImagePath().isEmpty()) {
                // If no image data, check if imagePath is available
                imageView = new ImageView(new Image(movie.getImagePath()));
            } else {
                // If neither image data nor imagePath, use a default image
                imageView = new ImageView(new Image("images/default_movie.png"));
            }

            imageView.setFitHeight(150);
            imageView.setFitWidth(100);

            VBox detailsBox = new VBox(5);
            Label title = new Label(movie.getTitle());
            Label description = new Label(movie.getDescription());
            detailsBox.getChildren().addAll(title, description);

            movieBox.getChildren().addAll(imageView, detailsBox);
            moviesContainer.getChildren().add(movieBox);
        }
    }


    @FXML
    private void handleBack() {
        try {
            App.goBack();  // חזרה לחלון הקודם
        } catch (IOException e) {
            showAlert("Error", "Failed to load the previous screen.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
