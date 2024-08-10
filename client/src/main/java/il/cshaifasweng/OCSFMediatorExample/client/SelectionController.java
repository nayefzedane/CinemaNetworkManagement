package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import java.io.IOException;

public class SelectionController {

    @FXML
    private void handleCustomer() throws IOException {
        // Navigate directly to the Customer dashboard
        App.setRoot("customer_dashboard");
    }

    @FXML
    private void handleAdmin() throws IOException {
        // Navigate to the login screen
        App.setRoot("login");
    }
}
