package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SubmitComplaint {

    @FXML
    private Label submitComplaintLabel;

    @FXML
    public void initialize() {
        submitComplaintLabel.setText("Submit Complaint Window Initialized");
    }

}
