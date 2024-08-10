package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;

public class CustomerController {

    @FXML
    private void handleViewMovies() {
        showAlert("View Movies", "This feature is under development.");
    }

    @FXML
    private void handlePurchaseTickets() {
        showAlert("Purchase Tickets", "This feature is under development.");
    }

    @FXML
    private void handleBack() {
        try {
            App.goBack();  // Use the goBack method to return to the previous screen
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
