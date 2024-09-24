package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
//import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
//import javafx.scene.control.ToggleGroup;
//import javafx.application.Platform;
//import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import java.io.IOException;

public class WatchScreenController {

    @FXML
    private TextField linkField;

    @FXML
    public void handleViewMovieButton() {
        String link = linkField.getText();
        if (link == null || link.isEmpty()) {
            showErrorAlert("Link cannot be empty");
            //return;

        }
        else {
            System.out.println(link);
            try {
                SimpleClient.getClient().sendToServer("CheckLink:"+ link);
                System.out.println(link);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }



    /*
    // Method to display a regular alert
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to display a temporary alert for a specified amount of time (in milliseconds)
    private void showTemporaryAlert(String title, String content, Alert.AlertType alertType, int displayDuration) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);

        // Show the alert
        alert.show();

        // Create a new thread to close the alert after the specified duration
        new Thread(() -> {
            try {
                Thread.sleep(displayDuration); // Sleep for the specified time (in milliseconds)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.close();  // Close the alert window
            });
        }).start();
    }
    */
    // Show success alert with refund value, but after 2 seconds delay
    public static void showSuccessAlert(String message) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));  // Create a 2-second delay
        delay.setOnFinished(event -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Checking link Succeeded!");
            alert.setHeaderText(null);
            alert.setContentText(message + "\nEnjoy watching the movie!\n");
            alert.show();  // Use show() instead of showAndWait() to avoid blocking the UI thread
        });
        delay.play();  // Start the delay
    }

    // Show failure alert with the error message, but after 2 seconds delay
    public static void showFailureAlert(String errorMessage) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));  // Create a 2-second delay
        delay.setOnFinished(event -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Checking link Failed!");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.show();  // Use show() instead of showAndWait() to avoid blocking the UI thread
        });
        delay.play();  // Start the delay
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
