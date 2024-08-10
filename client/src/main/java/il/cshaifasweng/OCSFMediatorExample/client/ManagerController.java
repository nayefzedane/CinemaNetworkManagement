package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ManagerController {

    @FXML
    private void handleManageMovies() {
        showAlert("Manage Movies", "This feature is under development.");
    }

    @FXML
    private void handleViewReports() {
        showAlert("View Reports", "This feature is under development.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
