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
    private LoginController loginController;
    private ReturnTicket returnTicketController;
    private BuyTicketPackageController buyTicketPackageController;
    private SubmitComplaint submitComplaintController;

    private Object activeController;

    @FXML
    public void initialize() {
        System.out.println("MainWindowController initialized.");
        showOfflineMovies(); // הצגת חלון סרטים לא אונליין כברירת מחדל
    }

    public void showOfflineMovies() {
        System.out.println("Loading Offline Movies Window...");
        loadWindow("OfflineMoviesWindow.fxml", OfflineMoviesController.class);
    }

    public void showOnlineMovies() {
        System.out.println("Loading Online Movies Window...");
        loadWindow("OnlineMoviesWindow.fxml", OnlineMoviesController.class);
    }

    public void showLoginWindow() {
        System.out.println("Loading Login Window...");
        loadWindow("login.fxml", LoginController.class);
    }

    public void showReturnTicketWindow() {
        System.out.println("Loading Return Ticket Window...");
        loadWindow("return_ticket.fxml", ReturnTicket.class);
    }

    public void showBuyTicketPackageWindow() {
        System.out.println("Loading Buy Ticket Package Window...");
        loadWindow("buy_ticket_package.fxml", BuyTicketPackageController.class);
    }

    public void showSubmitComplaintWindow() {
        System.out.println("Loading Submit Complaint Window...");
        loadWindow("submit_complaint.fxml", SubmitComplaint.class);
    }

    // מתודה פרטית שמטפלת בטעינת קובץ FXML והחלפת התוכן בחלון המרכזי
    private void loadWindow(String fxmlFile, Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(loader.load());

            Object controller = loader.getController();
            activeController = controller; // שמירת הקונטרולר הפעיל
            System.out.println("Loaded " + controllerClass.getName() + ": " + controller.getClass().getName());

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

    public ReturnTicket getReturnTicketController() {
        return returnTicketController;
    }

    public BuyTicketPackageController getBuyTicketPackageController() {
        return buyTicketPackageController;
    }

    public SubmitComplaint getSubmitComplaintController() {
        return submitComplaintController;
    }

    public Object getActiveController() {
        return activeController;
    }
}
