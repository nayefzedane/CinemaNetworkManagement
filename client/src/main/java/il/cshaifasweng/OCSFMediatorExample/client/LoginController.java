package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import java.io.IOException;



public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView backgroundImage;

    private SimpleClient client;

    @FXML
    public void initialize() {
        client = SimpleClient.getClient();

        // טעינת תמונת הרקע
        Image bgImage = new Image(getClass().getResourceAsStream("/images/background_login.png"));
        backgroundImage.setImage(bgImage);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            if (!client.isConnected()) {
                client.openConnection();
            }
            client.sendToServer("login@" + username + "@" + password);
        } catch (IOException e) {
            showError("Failed to connect to the server.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            App.goBack();  // חזרה לחלון הקודם
        } catch (IOException e) {
            showError("Failed to load the previous screen.");
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
