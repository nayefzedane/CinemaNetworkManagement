package il.cshaifasweng.OCSFMediatorExample.client;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.PackageCard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDate;

public class BuyTicketPackageController {

    @FXML
    public Button buyPackageButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField customerIdField;

    @FXML
    private TextField customerEmailField;

    @FXML
    private TextField fullCardNumberField;


    @FXML
    public void initialize() {
        // Initialization logic can go here if needed
    }

    @FXML
    private void handleBuyPackageButton() {
        // Retrieve data from fields
        String name = nameField.getText();
        String customerIdStr = customerIdField.getText();
        String customerEmail = customerEmailField.getText();
        String fullCardNumber = fullCardNumberField.getText();

        // Validate the input (example: check if fields are empty or if the card number is valid)
        if (name.isEmpty() || customerIdStr.isEmpty() || customerEmail.isEmpty() || fullCardNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (!fullCardNumber.matches("\\d{16}")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Card number must be 16 digits.");
            return;
        }

        int customerId;
        try {
            customerId = Integer.parseInt(customerIdStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Customer ID must be a valid number.");
            return;
        }

        // Extract the last 4 digits from the full card number
        String lastFourDigits = fullCardNumber.substring(fullCardNumber.length() - 4);

        // Set the purchase date as the current date
        LocalDate purchaseDate = LocalDate.now();

        // Price is fixed at $400 for this package
        double price = 400;

        // Create a new PackageCard entity
        PackageCard packageCard = new PackageCard(purchaseDate, price, customerId, customerEmail, Integer.parseInt(lastFourDigits), name);

        // Send the PackageCard to the server
        try {
            SimpleClient.getClient().sendToServer(packageCard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ShowReceipt(PackageCard packageCard) {
        StringBuilder receiptMessage = new StringBuilder();
        receiptMessage.append("Receipt:\n");
        receiptMessage.append("=====================================\n");
        receiptMessage.append("Package ID: ").append(packageCard.getPackageId()).append("\n");  // Package ID now correctly populated
        receiptMessage.append("Name: ").append(packageCard.getName()).append("\n");
        receiptMessage.append("Customer ID: ").append(packageCard.getCustomerId()).append("\n");
        receiptMessage.append("Customer Email: ").append(packageCard.getCustomerEmail()).append("\n");
        receiptMessage.append("Price: $").append(packageCard.getPrice()).append("\n");
        receiptMessage.append("Remaining Tickets: ").append(packageCard.getRemainingEntries()).append("\n");
        receiptMessage.append("Payment (Last 4 digits): ").append(packageCard.getPaymentLastFourDigits()).append("\n");
        receiptMessage.append("Purchase Date: ").append(packageCard.getPurchaseDate()).append("\n");
        receiptMessage.append("=====================================\n");
        receiptMessage.append("Note: Please remember your purchase ID number ").append(packageCard.getPackageId()).append(", as it will be required when you buy movie tickets.\n");

        // Ensure the alert dialog is shown on the JavaFX Application Thread
        Platform.runLater(() -> {
            System.out.println(receiptMessage.toString());
            // Show the receipt in an alert dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Purchase Receipt");
            alert.setHeaderText(null);  // Optional: remove header text
            alert.setContentText(receiptMessage.toString());
            alert.showAndWait();  // Use showAndWait to block until the user closes the alert
        });
    }

    // Helper method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
