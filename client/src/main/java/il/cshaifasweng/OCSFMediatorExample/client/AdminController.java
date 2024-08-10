package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AdminController {

    @FXML
    private void handleManageUsers() {
        showAlert("Manage Users", "This feature is under development.");
    }

    @FXML
    private void handleSystemSettings() {
        showAlert("System Settings", "This feature is under development.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
