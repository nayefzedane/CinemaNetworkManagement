package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;

public class AdminController {
    String pageName = "";

    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private TableView<purchaseCard> purchaseTableView;
    @FXML
    private TableColumn<purchaseCard, Integer> order_id;
    @FXML
    private TableColumn<purchaseCard, String> movie_title;
    @FXML
    private TableColumn<purchaseCard, String> branch_name;
    @FXML
    private TableColumn<purchaseCard, Float> price;
    @FXML
    private TableColumn<purchaseCard, LocalDate> purchase_date;
    @FXML
    private TableColumn<purchaseCard, Integer> costumer_id;
    @FXML
    private TableColumn<purchaseCard, Integer> payment_card_last_four;
    //this is for price requests now:
    @FXML
    private TableView<Request> requestTableView;
    @FXML
    private TableColumn<Request, Long> idColumn;
    @FXML
    private TableColumn<Request, String> titleColumn;
    @FXML
    private TableColumn<Request, String> descriptionColumn;
    @FXML
    private Button approveButton;
    @FXML
    private Button denyButton;

    private SimpleClient client;
    private ObservableList<purchaseCard> purchaseList; // the entire list
    private ObservableList<Request> requestList;



    private void loadTicketsSoldReport() {
        System.out.println("load tickets sold method called");
        // Send request to server to get purchase data
        try {

            SimpleClient.getClient().sendToServer("request_ticket_report");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to request ticket report from the server.");
        }
    }
    // Method to update the TableView with the data received from the server
    public void updatePurchaseList(List<purchaseCard> purchases) {
        purchaseList = FXCollections.observableArrayList(purchases);
        filterPurchaseList(); // Apply filtering after loading data
    }
    //method to filter the purchases list to the selected month
    private void filterPurchaseList() {
        String selectedMonth = monthComboBox.getValue();
        if (selectedMonth != null) {
            ObservableList<purchaseCard> filteredList = FXCollections.observableArrayList();
            int monthNumber = monthToNumber(selectedMonth);

            for (purchaseCard purchase : purchaseList) {
                if (purchase.getPurchaseDate().getMonthValue() == monthNumber) {
                    filteredList.add(purchase);
                }
            }

            purchaseTableView.setItems(filteredList);
        } else {
            purchaseTableView.setItems(purchaseList); // Show all if no month is selected
        }
    }

    private int monthToNumber(String month) {
        switch (month) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }




    private void viewPackagesAndMoviesReport(ActionEvent event) {
        // TODO: Implement the logic to handle the viewing of packages and online movies sold report
        showAlert("Packages & Online Movies Report", "This feature is under development.");
    }


    private void viewComplaintsReport(ActionEvent event) {
        // TODO: Implement the logic to handle the viewing of complaints report
        showAlert("Complaints Report", "This feature is under development.");
    }


    private void loadChangePricesRequests() {
        System.out.println("loadChangePricesRequests method called");  // Add this lin
        try {
            SimpleClient.getClient().sendToServer("request_price_change_requests");
            System.out.println("Requesting price change requests from server");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending request to server: " + e.getMessage());  // Add this line
        }
    }
    public void updateRequestList(List<Request> requests) {
        requestList = FXCollections.observableArrayList(requests);
        requestTableView.setItems(requestList);
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        Request selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            try {
                // Send description to the server
                //SimpleClient.getClient().sendToServer(selectedRequest.getDescription());
                // Delete the request from the server
                SimpleClient.getClient().sendToServer("change_request " + selectedRequest.getDescription());
                SimpleClient.getClient().sendToServer("delete_request " + selectedRequest.getId());
                // Remove the request from the table
                requestList.remove(selectedRequest);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to approve the request.");
            }
        } else {
            showAlert("No Selection", "Please select a request to approve.");
        }
    }
    @FXML
    private void handleDeny(ActionEvent event) {
        Request selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            try {
                // Delete the request from the server
                SimpleClient.getClient().sendToServer("delete_request " + selectedRequest.getId());
                // Remove the request from the table
                requestList.remove(selectedRequest);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to deny the request.");
            }
        } else {
            showAlert("No Selection", "Please select a request to deny.");
        }
    }






    @FXML
    public void initialize(){
        client = SimpleClient.getClient();  // Assign to the instance variable, not a new local variable
        client.setAdminController(this);
        System.out.println("AdminController initialized and set in SimpleClient.");

        }








    public void initialize_tickets(){
        System.out.println("initialize_tickets method called");
        client = SimpleClient.getClient();  // Assign to the instance variable, not a new local variable
        client.setAdminController(this);
        System.out.println("initialize_tickets method called and client set");

        System.out.println("order_id is " + (order_id == null ? "null" : "not null"));
        order_id.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        movie_title.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        branch_name.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        purchase_date.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        costumer_id.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        payment_card_last_four.setCellValueFactory(new PropertyValueFactory<>("paymentCardLastFour"));


        //load the data from the server
        System.out.println("Before calling loadTicketsSoldReport");  // Add this line
        loadTicketsSoldReport();
    }


    public void initialize_requests(){

        System.out.println("initialize_requests method called");  // Add this line
        // Initialize the table columns of requests
        client = SimpleClient.getClient();  // Assign to the instance variable, not a new local variable
        client.setAdminController(this);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        //load the data from server
        loadChangePricesRequests();
    }

    public void Ticket_Report(ActionEvent event){
        // If the purchaseList is already loaded, filter based on the selected month
        if (purchaseList != null) {
            filterPurchaseList();
        } else {
            // Load the data from the server if not already loaded
            initialize_tickets();
        }
    }
    public void Request_Report(ActionEvent event){
        initialize_requests();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleGoTo(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        String fxmlFile = "";

        switch (clickedButton.getText()) {
            case "Tickets Report":
                fxmlFile = "admin_tickets_report";
                break;
            case "Packages,Online Movies Report":
                fxmlFile = "admin_tickets_report";  // Assuming you have another FXML file
                break;
            case "Complaints Report":
                fxmlFile = "admin_tickets_report";  // Assuming you have another FXML file
                break;
            case "Change Prices Requests":
                fxmlFile = "admin_price_change";
                break;
        }

        System.out.println("Attempting to load FXML: " + fxmlFile);  // Add this line

        try {
            pageName = fxmlFile;
            App.setRoot(fxmlFile);
            System.out.println("FXML loaded successfully: " + fxmlFile);  // Add this line
            if (fxmlFile.equals("admin_tickets_report")) {
                System.out.println("Initializing tickets");  // Add this line
                //initialize_tickets();
                //loadTicketsSoldReport();
            } else if (fxmlFile.equals("admin_price_change")) {
                System.out.println("Initializing requests");  // Add this line
                //initialize_requests();
                //loadChangePricesRequests();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to navigate to " + fxmlFile);
        }
    }




    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            App.goBack();

        } catch (IOException e) {

            showAlert("Error", "Failed to return to the previous page.");
        }
    }
}