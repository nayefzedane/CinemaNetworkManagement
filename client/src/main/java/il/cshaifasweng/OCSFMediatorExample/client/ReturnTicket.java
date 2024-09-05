package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import java.io.IOException;

public class ReturnTicket {

    @FXML
    private TextField orderIdField;

    @FXML
    private TextField customerIdField;

    @FXML
    private RadioButton purchaseCardOption;

    @FXML
    private RadioButton purchaseLinkOption;

    private ToggleGroup purchaseTypeToggleGroup;

    @FXML
    public void initialize() {
        // Initialize the ToggleGroup and assign it to the radio buttons
        purchaseTypeToggleGroup = new ToggleGroup();
        purchaseCardOption.setToggleGroup(purchaseTypeToggleGroup);
        purchaseLinkOption.setToggleGroup(purchaseTypeToggleGroup);
    }

    @FXML
    private void handleReturnTicket() {
        String orderId = orderIdField.getText();
        String customerId = customerIdField.getText();
        RadioButton selectedPurchaseType = (RadioButton) purchaseTypeToggleGroup.getSelectedToggle();

        // Validate input fields
        if (orderId.isEmpty() || customerId.isEmpty() || selectedPurchaseType == null) {
            showAlert("Input Error", "Please make sure all fields are filled out:\n- Order ID\n- Customer ID\n- Purchase Type", Alert.AlertType.ERROR);
            return;
        }

        // If everything is filled out
        String purchaseType = selectedPurchaseType.getText();
        String message = String.format("Return Ticket,%s,%s,%s", purchaseType, orderId, customerId);
        System.out.println(message);
        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Send message to the server (placeholder for your actual server communication logic)
        // SimpleClient.getInstance().sendMessageToServer(message);

        // Show success alert for 2 seconds
        showTemporaryAlert("Request Sent", "The request to return the ticket has been sent successfully!", Alert.AlertType.INFORMATION, 2000);
    }

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
    // Show success alert with refund value, but after 2 seconds delay
    public static void showSuccessAlert(float refundAmount) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));  // Create a 2-second delay
        delay.setOnFinished(event -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Ticket Returned Successfully");
            alert.setHeaderText(null);
            alert.setContentText("You have returned your ticket successfully. You will receive a refund of " + refundAmount + ".");
            alert.show();  // Use show() instead of showAndWait() to avoid blocking the UI thread
        });
        delay.play();  // Start the delay
    }

    // Show failure alert with the error message, but after 2 seconds delay
    public static void showFailureAlert(String errorMessage) {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));  // Create a 2-second delay
        delay.setOnFinished(event -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Failed to Return Ticket");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.show();  // Use show() instead of showAndWait() to avoid blocking the UI thread
        });
        delay.play();  // Start the delay
    }
}
