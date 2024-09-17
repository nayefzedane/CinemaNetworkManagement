package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;

import java.io.IOException;


public class CustomerServiceController {

     static Object activeController;
    private ComplainsHandle complainsHandle;
   private HandleComplainsInquiries handleComplainsInquiries;


    @FXML
    private StackPane contentPane;

  //  @FXML

    @FXML
    public void initialize() {
        if (contentPane != null) {
            System.out.println("contentPane initialized successfully.");
        } else {
            System.err.println("contentPane is not initialized properly.");
        }
    }

    // private void handleComplaints() {
      //  showAlert("Handle Complaints", "This feature is under development.");
   // }

   // @FXML
    //private void handleInquiries() {
      //  showAlert("View Customer Inquiries", "This feature is under development.");
  // }


    @FXML
    public void handleComplaints() {
        System.out.println("Loading ComplainsHandle...");
        loadWindow("/il/cshaifasweng/OCSFMediatorExample/client/ComplainsHandle.fxml", ComplainsHandle.class);
    }

    @FXML
    public void handleInquiries() {
        System.out.println("Loading HandleComplainsInquiries...");
        loadWindow("/il/cshaifasweng/OCSFMediatorExample/client/HandleComplainsInquiries.fxml", HandleComplainsInquiries.class);
    }



    private void loadWindow(String fxmlFile, Class<?> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            if (contentPane == null) {
                System.err.println("Error: contentPane is not initialized.");
                return;
            }

            // Clear existing content from the contentPane
            System.out.println("Children count before clear: " + contentPane.getChildren().size());
            contentPane.getChildren().clear();
            System.out.println("Children count after clear: " + contentPane.getChildren().size());
            // Force layout to ensure old content is removed
            contentPane.layout();

            // Load the new FXML and add it to the contentPane
            contentPane.getChildren().add(loader.load());

            // Ensure only one node exists in the contentPane
            if (contentPane.getChildren().size() > 1) {
                System.err.println("Warning: More than one node found in contentPane after loading new content.");
            }

            // Set the active controller
            activeController = loader.getController();

            System.out.println("Loaded " + controllerClass.getName() + ": " + activeController.getClass().getName());

        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while loading: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public static Object getActiveController() {
        return activeController;
    }

    public ComplainsHandle getComplainsHandle() {
        return complainsHandle;
    }
    public HandleComplainsInquiries getHandleComplainsInquiries(){
        return handleComplainsInquiries;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
