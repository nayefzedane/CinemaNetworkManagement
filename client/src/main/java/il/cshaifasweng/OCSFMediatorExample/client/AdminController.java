package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.PurchaseLink;
import il.cshaifasweng.OCSFMediatorExample.entities.PackageCard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javafx.application.Platform;

public class AdminController {
    private static String viewType = "Admin";


    public static void setViewType(String viewType1) {
        System.out.println("we are in set view type method and the new view type is: " + viewType);

        if (viewType1.equals("admin haifa")) {
            viewType = "Yes Planet";
        } else if (viewType1.equals("admin nazareth")) {
            viewType = "Cinema City";
        } else {
            viewType = "Admin";
        }


        System.out.println("Updated viewType is now: " + viewType);  // Print the updated value
    }
    public static String getViewType() {
        return viewType;
    }
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
    //this is for link table
    @FXML
    private TableView<PurchaseLink> purchaseLinkTableView;
    @FXML
    private TableColumn<PurchaseLink, Integer> link_id;
    @FXML
    private TableColumn<PurchaseLink, String> movie_title_link;
    @FXML
    private TableColumn<PurchaseLink, Integer> customer_id;
    @FXML
    private TableColumn<PurchaseLink, LocalDateTime> purchase_time;
    @FXML
    private TableColumn<PurchaseLink, String> customer_mail;
    @FXML
    private TableColumn<PurchaseLink, Float> price_link;
    @FXML
    private Label totalIncomeLabel;
    //this for packages:
    @FXML
    private TableView<PackageCard> packageTableView;
    @FXML
    private TableColumn<PackageCard, Integer> package_id;
    @FXML
    private TableColumn<PackageCard, Integer> remaining_entries;
    @FXML
    private TableColumn<PackageCard, LocalDate> purchase_date_package;
    @FXML
    private TableColumn<PackageCard, Integer> customer_id_package;
    @FXML
    private TableColumn<PackageCard, String> customer_email;
    @FXML
    private TableColumn<PackageCard, Double> price_package;

    private SimpleClient client;
    private ObservableList<purchaseCard> purchaseList; // the entire list
    private ObservableList<Request> requestList;
    private ObservableList<PurchaseLink> purchaseLinkList;
    private ObservableList<PackageCard> packageCardList;




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
    public void updatePackageList(List<PackageCard> packages) {
        packageCardList = FXCollections.observableArrayList(packages);
        filterPackageList(); // Apply filtering after loading data
    }

    // Method to update the TableView with the data received from the server
    public void updatePurchaseList(List<purchaseCard> purchases) {
        purchaseList = FXCollections.observableArrayList(purchases);
        filterPurchaseList(); // Apply filtering after loading data

    }

    //method to filter the purchases list to the selected month
    private void filterPurchaseList() {
        // Print the current viewType for debugging purposes
        System.out.println("we are in filter purchase and this is " + viewType);

        // Get the selected month from the ComboBox
        String selectedMonth = monthComboBox.getValue();
        double totalIncome = 0.0; // Initialize total income

        // Create a list to hold the filtered purchases
        ObservableList<purchaseCard> filteredList = FXCollections.observableArrayList();

        // If a month is selected, filter by both branch name and month
        if (selectedMonth != null) {
            int monthNumber = monthToNumber(selectedMonth); // Convert month name to its corresponding number

            // Iterate through all purchases to apply the filters
            for (purchaseCard purchase : purchaseList) {
                boolean matchesMonth = purchase.getPurchaseDate().getMonthValue() == monthNumber; // Check if purchase month matches
                boolean matchesBranch = viewType.equals("Admin") || purchase.getBranchName().equals(viewType); // Check if purchase branch matches or if viewType is Admin

                // If both the month and branch match, add the purchase to the filtered list and update total income
                if (matchesMonth && matchesBranch) {
                    filteredList.add(purchase);
                    totalIncome += purchase.getPrice(); // Add to total income
                }
            }

            // Set the filtered list in the TableView
            purchaseTableView.setItems(filteredList);
        } else {
            // If no month is selected, filter only by branch name
            for (purchaseCard purchase : purchaseList) {
                boolean matchesBranch = viewType.equals("Admin") || purchase.getBranchName().equals(viewType); // Check if purchase branch matches or if viewType is Admin

                // If the branch matches, add the purchase to the filtered list and update total income
                if (matchesBranch) {
                    filteredList.add(purchase);
                    totalIncome += purchase.getPrice(); // Add to total income
                }
            }

            // Set the filtered list in the TableView
            purchaseTableView.setItems(filteredList);

            // Calculate total income for all purchases in the filtered list
            totalIncome = filteredList.stream()
                    .mapToDouble(purchaseCard::getPrice)
                    .sum();
        }

        // Update the total income label with the calculated total income
        totalIncomeLabel.setText(String.format("%.2f", totalIncome));
    }

    private void filterPackageList() {
        String selectedMonth = monthComboBox.getValue();
        double totalIncome = 0.0; // Initialize total income

        if (selectedMonth != null) {
            ObservableList<PackageCard> filteredList = FXCollections.observableArrayList();
            int monthNumber = monthToNumber(selectedMonth);

            for (PackageCard packageCard : packageCardList) {
                if (packageCard.getPurchaseDate().getMonthValue() == monthNumber) {
                    filteredList.add(packageCard);
                    totalIncome += packageCard.getPrice(); // Add to total income
                }
            }

            packageTableView.setItems(filteredList);
        } else {
            packageTableView.setItems(packageCardList); // Show all if no month is selected
            // Calculate total income for all packages
            totalIncome = packageCardList.stream()
                    .mapToDouble(PackageCard::getPrice)
                    .sum();
        }

        // Update the total income label
        totalIncomeLabel.setText(String.format("%.2f", totalIncome));
    }

    public void filterPurchaseLink(){
        String selectedMonth = monthComboBox.getValue();
        double totalIncome = 0.0; // Initialize total income
        if (selectedMonth != null) {
            ObservableList<PurchaseLink> filteredListLink = FXCollections.observableArrayList();
            int monthNumber = monthToNumber(selectedMonth);

            for (PurchaseLink purchase : purchaseLinkList) {
                if (purchase.getPurchaseTime().getMonthValue() == monthNumber) {
                    filteredListLink.add(purchase);
                    totalIncome += purchase.getPrice(); // Add to total income
                }
            }

            purchaseLinkTableView.setItems(filteredListLink);
        } else {
            purchaseLinkTableView.setItems(purchaseLinkList); // Show all if no month is selected
            // Calculate total income for all purchases
            totalIncome = purchaseLinkList.stream()
                    .mapToDouble(PurchaseLink::getPrice)
                    .sum();
        }

        // Update the total income label
        totalIncomeLabel.setText(String.format("%.2f", totalIncome));
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
    public void loadLinks(){
        System.out.println("loadLinks method called");
        try {
            SimpleClient.getClient().sendToServer("request_purchase_link_report");
            System.out.println("Requesting links from server");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending request for links to server: " + e.getMessage());  // Add this line
        }
    }
    public void loadPackages() {
        System.out.println("loadPackages method called");
        try {
            SimpleClient.getClient().sendToServer("request_package_report");
            System.out.println("Requesting packages from server");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending request for packages to server: " + e.getMessage());
        }
    }

    public void updatePurchaseLinkList(List<PurchaseLink> purchases) {
        purchaseLinkList = FXCollections.observableArrayList(purchases);
        filterPurchaseLink(); // Apply filtering after loading data
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
    public void initialize_Links(){
        System.out.println("initialize_Links method called");
        client = SimpleClient.getClient();  // Assign to the instance variable, not a new local variable
        client.setAdminController(this);

        link_id.setCellValueFactory(new PropertyValueFactory<>("linkId"));
        movie_title_link.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        customer_id.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        purchase_time.setCellValueFactory(new PropertyValueFactory<>("purchaseTime"));
        customer_mail.setCellValueFactory(new PropertyValueFactory<>("customerMail"));
        price_link.setCellValueFactory(new PropertyValueFactory<>("price"));

        //load the data from server
        loadLinks();
    }
    public void initialize_Packages() {
        System.out.println("initialize_Packages method called");
        client = SimpleClient.getClient();  // Assign to the instance variable, not a new local variable
        client.setAdminController(this);

        package_id.setCellValueFactory(new PropertyValueFactory<>("packageId"));
        remaining_entries.setCellValueFactory(new PropertyValueFactory<>("remainingEntries"));
        purchase_date_package.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        customer_id_package.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customer_email.setCellValueFactory(new PropertyValueFactory<>("customerEmail"));
        price_package.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Load the data from server
        loadPackages();
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
    public void Link_Report(ActionEvent event){
        // If the purchaseList is already loaded, filter based on the selected month
        if (purchaseLinkList != null) {
            filterPurchaseLink();
        } else {
            // Load the data from the server if not already loaded
            initialize_Links();
        }
    }
    public void Package_Report(ActionEvent event){
        if (packageCardList != null){
            filterPackageList();
        } else {
            initialize_Packages();
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
            case "Online Movies Report":
                fxmlFile = "admin_links_report";  // Assuming you have another FXML file
                break;
            case "Complaints Report":
                fxmlFile = "admin_tickets_report";  // Assuming you have another FXML file
                break;
            case "Change Prices Requests":
                fxmlFile = "admin_price_change";
                break;
            case "Packages Report":
                fxmlFile = "admin_package_report";
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