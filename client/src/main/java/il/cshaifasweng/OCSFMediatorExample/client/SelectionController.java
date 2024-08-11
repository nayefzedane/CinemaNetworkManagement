package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class SelectionController {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Button customerButton;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        // טוען את תמונת הרקע
        Image background = new Image(getClass().getResourceAsStream("/images/selection.png"));
        backgroundImage.setImage(background);

        // התאמת הכפתורים כך שיהיו שקופים
        makeButtonTransparent(customerButton);
        makeButtonTransparent(loginButton);

        // הגדרת פעולות לכפתורים
        customerButton.setOnMouseEntered(this::onMouseEnter);
        customerButton.setOnMouseExited(this::onMouseExit);
        customerButton.setOnAction(event -> {
            try {
                App.setRoot("customer_dashboard");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        loginButton.setOnMouseEntered(this::onMouseEnter);
        loginButton.setOnMouseExited(this::onMouseExit);
        loginButton.setOnAction(event -> {
            try {
                App.setRoot("login");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void makeButtonTransparent(Button button) {
        button.setStyle("-fx-background-color: transparent;");
    }

    private void onMouseEnter(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-border-color: green; -fx-border-width: 2px;");
    }

    private void onMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-color: transparent;");
    }
}
