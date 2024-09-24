package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaints;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class SubmitComplaint {

    @FXML
    public TextField complaintSearch;
    @FXML
    private VBox complainBox;

    @FXML
    private Label submitComplaintLabel;

    @FXML
    private TextField complainMail;

    @FXML
    private TextField complainTitle;

    @FXML
    private TextField complainText;

    @FXML
    private MenuButton selectedBranch;

    @FXML
    private TextField complainSearch;

    @FXML
    private MenuItem buttonCinemaCity;

    @FXML
    private MenuItem buttonYesPlanet;

    @FXML
    public void initialize() {
        submitComplaintLabel.setText("Submit Complaint Window");
        // You can add additional setup here if needed
    }

    // Event handler method to update the MenuButton text for Cinema City
    @FXML
    private void handleCinemaCitySelection(ActionEvent event) {
        selectedBranch.setText("Cinema City");
        // Additional logic for Cinema City selection can be added here
    }

    // Event handler method to update the MenuButton text for Yes Planet
    @FXML
    private void handleYesPlanetSelection(ActionEvent event) {
        selectedBranch.setText("Yes Planet");
        // Additional logic for Yes Planet selection can be added here
    }

    // Method to handle complaint submission
    @FXML
    void submitComplaint(ActionEvent event) {
        // Get input values from text fields
        String email = complainMail.getText();
        String title = complainTitle.getText();
        String content = complainText.getText();
        String branch = selectedBranch.getText();

        // Basic validation for empty fields
        if (email.isEmpty() || title.isEmpty() || content.isEmpty()) {
            showAlert("Error", "Please fill all fields before submitting.");
            return;
        }

        // Check if branch selection is default
        if ("Branch".equals(branch)) {
            branch = ""; // Reset if the user hasn't selected a valid branch
        }

        // Capture the current time
        LocalDateTime timeSubmitted = LocalDateTime.now();

        // Create a Complaints object with the gathered information
        Complaints complaint = new Complaints(email,
                branch,
                title,
                content,
                timeSubmitted);

        try {
            // Send the complaint to the server
            SimpleClient.getClient().sendToServer(complaint);
            // Show success message after successful submission
            showAlert("Success", "Complaint submitted successfully!,Your complaint will get respond in 24h");
            clearFields();
        } catch (IOException e) {
            // Show error message if the complaint submission fails
            showAlert("Error", "Failed to submit complaint. Please try again.");
            e.printStackTrace();
        }
    }
    private void clearFields() {
        complainMail.clear();
        complainTitle.clear();
        complainText.clear();
        selectedBranch.setText("Branch"); // Reset MenuButton to its default state
    }
    // Utility method to display alerts with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
