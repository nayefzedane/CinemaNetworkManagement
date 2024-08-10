package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CustomerServiceController {

    @FXML
    private void handleComplaints() {
        showAlert("Handle Complaints", "This feature is under development.");
    }

    @FXML
    private void handleInquiries() {
        showAlert("View Customer Inquiries", "This feature is under development.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
