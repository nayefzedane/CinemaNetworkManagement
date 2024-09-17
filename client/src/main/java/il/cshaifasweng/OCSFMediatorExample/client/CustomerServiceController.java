package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaints;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public class CustomerServiceController {

    @FXML
    private ListView<String> complaintsListView;  // ListView to display unanswered complaints

    @FXML
    private VBox detailsVBox;  // VBox that displays complaint details

    @FXML
    private Label emailLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private Label branchLabel;

    @FXML
    private Label timeSubmittedLabel;

    @FXML
    private TextArea answerTextArea;

    @FXML
    private TextField compensationField;

    //
    //   private List<Complaints> unansweredComplaints;  // List to hold the complaints fetched from the database
    private Complaints selectedComplaint;  // The currently selected complaint

    @FXML
    public void initialize() {
        // Add necessary initialization code here, e.g., loading initial data
        detailsVBox.setVisible(false);
        System.out.println("ComplainsHandle initialized.");
    }

    @FXML
    public void loadUnansweredComplaints() {
        try {
            // Send a request to the server to get unanswered complaints
            SimpleClient.getClient().sendToServer("getUnansweredComplaints");
        } catch (IOException e) {
            showAlert("Error", "Failed to send request to server.");
            e.printStackTrace();
        }
    }


    public void handleReceivedComplaints(List<Complaints> complaints) {
        complaintsListView.getItems().clear(); // Clear existing complaints in the list
        if (complaints == null || complaints.isEmpty()) {
            showAlert("Info", "No complaints found.");
            return;
        }
        for (Complaints complaint : complaints) {
            String complaintDetails = "ID: " + complaint.getId() + "\n" +
                  //  "Email: " + complaint.getMail() + "\n" +
                    "Title: " + complaint.getComplainTitle() + "\n" +
                  //  "Content: " + complaint.getComplainText() + "\n" +
                    "Branch: " + complaint.getBranch() + "\n" +
                    "Time Submitted: " + complaint.getComplainDate();
            complaintsListView.getItems().add(complaintDetails);
        }

        complaintsListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                selectedComplaint = complaints.get(newValue.intValue());
                showComplaintDetails(selectedComplaint);
            }
        });
    }

    private void showComplaintDetails(Complaints complaint) {
        emailLabel.setText(complaint.getMail());
        titleLabel.setText(complaint.getComplainTitle());
        contentTextArea.setText(complaint.getComplainText());
        branchLabel.setText(complaint.getBranch());
        timeSubmittedLabel.setText(complaint.getComplainDate().toString());

        // Make the VBox visible when a complaint is selected
        detailsVBox.setVisible(true);
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }
    @FXML
    private void submitAnswerAndCompensation() {
        if (selectedComplaint == null) {
            showAlert("Error", "Please select a complaint from the list.");
            return;
        }

        String answer = answerTextArea.getText().trim();
        String compensationStr = compensationField.getText().trim();

        if (answer.isEmpty()) {
            showAlert("Error", "Answer cannot be empty.");
            return;
        }

        double compensation;
        try {
            compensation = Double.parseDouble(compensationStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid compensation amount.");
            return;
        }

        // Update the selected complaint with the answer and compensation
        selectedComplaint.setAnswer(answer);
        selectedComplaint.setFinancialCompensation(compensation);
        selectedComplaint.setComplainDate(LocalDateTime.now());

        // Send the updated complaint to the server for processing
        sendUpdatedComplaintToServer(selectedComplaint);
    }

    // Method to send the updated complaint to the server
    private void sendUpdatedComplaintToServer(Complaints complaint) {
        if (complaint == null) {
            showAlert("Error", "No complaint selected.");
            return;
        }

        // Create the update message with a specific format
        String msg = "updateComplaint;" + complaint.getId() + ";" +
                complaint.getAnswer() + ";" +
                complaint.getFinancialCompensation() + ";" +
                complaint.getComplainDate();

        try {
            // Send the message to the server via SimpleClient
            SimpleClient.getClient().sendToServer(msg);

            removeComplaintFromList(complaint);

            showAlert("Success", "Complaint sent to server for updating.");
            clearComplaintDetails();
        } catch (IOException e) {
            showAlert("Error", "Failed to send update to server.");
            e.printStackTrace();
        }
    }

    private void removeComplaintFromList(Complaints complaint) {
        int selectedIndex = complaintsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            complaintsListView.getItems().remove(selectedIndex);
        }
    }

    private void clearComplaintDetails() {
        emailLabel.setText("");
        titleLabel.setText("");
        contentTextArea.clear();
        branchLabel.setText("");
        timeSubmittedLabel.setText("");
        answerTextArea.clear();
        compensationField.clear();
        detailsVBox.setVisible(false);
    }
    @FXML
    private void handleBack() {
        try {
            App.goBack();  // חזרה לחלון הקודם
        } catch (IOException e) {
            showAlert("Error" ,"Failed to load the previous screen." + e.getMessage());
        }
    }
}
