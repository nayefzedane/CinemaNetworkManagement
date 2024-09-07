package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BuyTicketPackageController {

    @FXML
    private Label buyTicketPackageLabel;

    @FXML
    public void initialize() {
        buyTicketPackageLabel.setText("Buy Ticket Package Window Initialized");
    }

}
