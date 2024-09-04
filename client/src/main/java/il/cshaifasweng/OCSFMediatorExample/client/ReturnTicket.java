package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReturnTicket {

    @FXML
    private Label returnTicketLabel;

    @FXML
    public void initialize() {
        returnTicketLabel.setText("Return Ticket Window Initialized");
    }

}
