package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private StackPane contentPane;

    private OfflineMoviesController offlineMoviesController;
    private OnlineMoviesController onlineMoviesController;
    private LoginController loginController;  // משתנה עבור ה-LoginController
    private Object activeController; // משתנה לשמירת הקונטרולר הפעיל

    @FXML
    public void initialize() {
        System.out.println("MainWindowController initialized.");
        showOfflineMovies(); // הצגת חלון סרטים לא אונליין כברירת מחדל
    }

    public void showOfflineMovies() {
        System.out.println("Loading Offline Movies Window...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OfflineMoviesWindow.fxml"));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(loader.load());

            offlineMoviesController = loader.getController();
            activeController = offlineMoviesController; // שמירת הקונטרולר הפעיל
            System.out.println("Loaded OfflineMoviesController: " + offlineMoviesController.getClass().getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showOnlineMovies() {
        System.out.println("Loading Online Movies Window...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OnlineMoviesWindow.fxml"));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(loader.load());

            onlineMoviesController = loader.getController();
            activeController = onlineMoviesController; // שמירת הקונטרולר הפעיל
            System.out.println("Loaded OnlineMoviesController: " + onlineMoviesController.getClass().getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginWindow() {
        System.out.println("Loading Login Window...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(loader.load());

            loginController = loader.getController();
            activeController = loginController; // שמירת הקונטרולר הפעיל
            System.out.println("Loaded LoginController: " + loginController.getClass().getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OfflineMoviesController getOfflineMoviesController() {
        return offlineMoviesController;
    }

    public OnlineMoviesController getOnlineMoviesController() {
        return onlineMoviesController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public Object getActiveController() {
        return activeController;
    }
}
