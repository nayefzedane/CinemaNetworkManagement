package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

import static il.cshaifasweng.OCSFMediatorExample.client.App.client;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.newHost;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.newPort;

public class InitConnection {

    @FXML
    private TextField host;

    @FXML
    private TextField port;

    @FXML
    public void initialize() {
        // אין צורך לטעון את התמונה בקוד, היא נטענת דרך CSS
    }

    @FXML
    void initC(ActionEvent event) {
        newHost = host.getText();
        newPort = Integer.parseInt(port.getText());
        client = SimpleClient.getClient();
        try {
            client.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("INIT REACHED...");
        Platform.runLater(() -> {
            try {
                App.setRoot("MainWindow");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void exitApp(ActionEvent event) {
        Platform.exit(); // סוגר את האפליקציה
    }
}
