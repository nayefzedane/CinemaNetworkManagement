package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ContentManagerController {


    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> titleColumn;
    @FXML private TableColumn<Movie, String> placeColumn;
    @FXML private TableColumn<Movie, String> showtimeColumn;
    @FXML private TableColumn<Movie, String> priceColumn;
    private ObservableList<Movie> movieList;

    @FXML private TextField titleField;
    @FXML private DatePicker showtimeDatePicker;
    @FXML private TextField showtimeHourField;
    @FXML private TextField showtimeMinuteField;
    @FXML private DatePicker releaseDatePicker;
    @FXML private TextField directorField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField priceField;
    @FXML private TextField durationField;
    @FXML private TextField ratingField;
    @FXML private TextField genreField;
    @FXML private TextField imagePathField;
    @FXML private ComboBox<String> placeComboBox;
    @FXML private ComboBox<String> hallNumberComboBox;
    @FXML private TextField producerField;
    @FXML private TextArea leadingActorsArea;

    private SimpleClient client;

    @FXML
    private Label movieCountLabel;

    @FXML
    private void initialize() {
        try {
            if (titleColumn != null) {
                titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
            }
            if (placeColumn != null) {
                placeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlace()));
            }
            if (showtimeColumn != null) {
                showtimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShowtime().toString()));
            }
            if (priceColumn != null) {
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            }
            if (titleColumn != null)
                loadMoviesIntoTable();

            // Initialize the ComboBox values for place and hall number
            if(placeComboBox != null)
                placeComboBox.setItems(FXCollections.observableArrayList("Cinema City", "Yes Planet"));
            if(hallNumberComboBox != null)
                hallNumberComboBox.setItems(FXCollections.observableArrayList("Hall 1", "Hall 2"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = SimpleClient.getClient();
        client.setContentManagerController(this);
        updateMovieCount();
    }

    private void loadMoviesIntoTable() {
        try {
            SimpleClient.getClient().sendToServer("get all movies"); // Request all movies from the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateMovieTable(List<Movie> movies) {
        movieList = FXCollections.observableArrayList(movies);
        movieTable.setItems(movieList);
    }
    public void updateMovieCount() {
        try {
            client.sendToServer("#getMovieCount");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMovieCount(int count) {
        Platform.runLater(() -> {
            if(movieCountLabel != null)
                movieCountLabel.setText("Movies in Database: " + count);
        });
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Movie Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleAddMovie() {
        try {
            String title = titleField.getText();
            LocalDateTime showtime = getShowtime();
            LocalDate releaseDate = releaseDatePicker.getValue();
            String director = directorField.getText();
            String description = descriptionArea.getText();
            int duration = Integer.parseInt(durationField.getText());
            float rating = Float.parseFloat(ratingField.getText());
            float price = Float.parseFloat(priceField.getText());
            String genre = genreField.getText();

            // Get new fields' values
            String place = placeComboBox.getValue();
            String hallNumber = hallNumberComboBox.getValue();
            String producer = producerField.getText();
            String leadingActors = leadingActorsArea.getText();

            byte[] imageData = null;
            File imageFile = new File(imagePathField.getText());

            if (imageFile.exists()) {
                try {
                    imageData = Files.readAllBytes(imageFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error reading the image file.");
                }
            } else {
                System.err.println("Image file does not exist at path: " + imagePathField.getText());
            }

            // Create the Movie object based on whether the image exists or not
            Movie movie = new Movie(title, showtime, releaseDate, genre, duration, rating, director, description, imageData, place, price, false, hallNumber.equals("Hall 1") ? 1 : 2, producer, leadingActors);

            // Send the movie to the server
            SimpleClient.getClient().sendToServer(movie);
            clearForm();

            // Show a confirmation message
            showAlert(Alert.AlertType.INFORMATION, "Movie Added", "The movie has been successfully added.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add movie: " + e.getMessage());
        }
    }

    private LocalDateTime getShowtime() {
        LocalDate date = showtimeDatePicker.getValue();
        int hour = Integer.parseInt(showtimeHourField.getText());
        int minute = Integer.parseInt(showtimeMinuteField.getText());
        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }

    @FXML
    public void handleDeleteMovie(ActionEvent actionEvent) {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            try {
                SimpleClient.getClient().sendToServer("delete movie " + selectedMovie.getId()); // Send delete request
                movieList.remove(selectedMovie);// Remove from list locally
                showAlert(Alert.AlertType.CONFIRMATION, "Confirmation", "Movie deleted successfully");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error" ,"Failed to delete movie" + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "NO MOVIE SELECTED" ,"Please select a movie");
        }
    }

    public void handleUpdateShowtime(ActionEvent actionEvent) {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            // Validate the date
            LocalDate selectedDate = showtimeDatePicker.getValue();
            LocalDate today = LocalDate.now();
            if (selectedDate == null || !selectedDate.isAfter(today)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Date", "Please select a future date.");
                return;
            }

            // Validate the time fields
            String hourText = showtimeHourField.getText();
            String minuteText = showtimeMinuteField.getText();
            int hour, minute;

            try {
                hour = Integer.parseInt(hourText);
                minute = Integer.parseInt(minuteText);

                if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Time", "Please enter a valid time (HH:MM).");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter numeric values for hours and minutes.");
                return;
            }

            try {
                SimpleClient.getClient().sendToServer("update showtime:" + selectedMovie.getId() + ":" + selectedDate.toString() +
                        ":" + hour + ":" + minute); // Send update showtime request
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update movie showtime: " + e.getMessage());
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Confirmation", "Movie showtime updated successfully \nNew show date and time are: " +
                    selectedDate.toString() + " , " + hour + ":" + minute);
            loadMoviesIntoTable();
        } else {
            showAlert(Alert.AlertType.ERROR, "No Movie Selected", "Please select a movie.");
        }
    }

    public void handleUpdatePrice(ActionEvent actionEvent) {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            try {
                double newPrice = Double.parseDouble(priceField.getText());
                if (newPrice >= 0) {
                    SimpleClient.getClient().sendToServer("send request:"+ selectedMovie.getTitle() + ":" + selectedMovie.getId() + ":" + selectedMovie.getPrice() + ":" + newPrice  + ":" + selectedMovie.getShowtime() + ":" + selectedMovie.getPlace());
                    showAlert(Alert.AlertType.INFORMATION, "Confirmation", "Request to update movie price sent successfully \n new price: " + newPrice);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Price", "Please enter a valid price greater than 0.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the price.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update movie price: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "No Movie Selected", "Please select a movie to update.");
        }
    }

    private void clearForm() {
        titleField.clear();
        showtimeHourField.clear();
        showtimeMinuteField.clear();
        directorField.clear();
        descriptionArea.clear();
        priceField.clear();
        genreField.clear();
        imagePathField.clear();
        ratingField.clear();
        durationField.clear();
        producerField.clear();
        leadingActorsArea.clear();

        if (!showtimeDatePicker.equals(null)) showtimeDatePicker.setValue(null);
        if (!releaseDatePicker.equals(null)) releaseDatePicker.setValue(null);
        if (!placeComboBox.equals(null)) placeComboBox.getSelectionModel().clearSelection();
        if (!hallNumberComboBox.equals(null)) hallNumberComboBox.getSelectionModel().clearSelection();
    }

    private void clearOnlineForm(){
        titleField.clear();
        directorField.clear();
        descriptionArea.clear();
        priceField.clear();
        genreField.clear();
        imagePathField.clear();
        ratingField.clear();
        durationField.clear();
        producerField.clear();
        leadingActorsArea.clear();

        if (!releaseDatePicker.equals(null)) releaseDatePicker.setValue(null);
    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleBack() {
        try {
            App.goBack();  // חזרה לחלון הקודם
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error" ,"Failed to load the previous screen." + e.getMessage());
        }
    }
    @FXML
    public void handleGoTo(ActionEvent actionEvent) {
        Button clickedButton = (Button) actionEvent.getSource();
        System.out.println("Navigating to " + clickedButton.getText());
        String fxmlFile = "";

        switch (clickedButton.getText()) {
            case "Add Movie":
                fxmlFile = "content_manager_addmovie";
                break;
            case "Delete Movie":
                fxmlFile = "content_manager_deletemovie";
                break;
            case "Update Price":
                fxmlFile = "content_manager_updateprice";
                break;
            case "Update Showtime":
                fxmlFile = "content_manager_updateshowtime";
                break;
            case "Add Online Movie":
                fxmlFile = "content_manager_addonlinemovie"; // Add this case for the new button
                break;
        }
        try {
            App.setRoot(fxmlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleAddOnlineMovie(ActionEvent actionEvent) {
        try {
            String title = titleField.getText();
            LocalDate releaseDate = (releaseDatePicker != null) ? releaseDatePicker.getValue() : LocalDate.now();
            String director = directorField.getText();
            String producer = producerField.getText();  // New field for producer
            String leadingActors = leadingActorsArea.getText();  // New field for leading actors
            String description = descriptionArea.getText();
            int duration = Integer.parseInt(durationField.getText());
            float rating = Float.parseFloat(ratingField.getText());
            float price = Float.parseFloat(priceField.getText());
            String genre = genreField.getText();
            byte[] imageData = null;
            File imageFile = new File(imagePathField.getText());

            if (imageFile.exists()) {
                try {
                    imageData = Files.readAllBytes(imageFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error reading the image file.");
                }
            } else {
                System.err.println("Image file does not exist at path: " + imagePathField.getText());
            }

            // Set default values for place, hallNumber, and showtime for online movies
            String place = "-";  // Default for online movies
            int hallNumber = 1;  // Default hall number for online movies
            LocalDateTime showtime = LocalDateTime.now() ;

            // Create and send the online movie object
            Movie movie = new Movie(title, showtime, releaseDate, genre, duration, rating, director, description, imageData, place, price, true, hallNumber, producer, leadingActors);
            SimpleClient.getClient().sendToServer(movie);
            clearOnlineForm();

            // Show a confirmation message
            showAlert(Alert.AlertType.INFORMATION, "Online Movie Added", "The online movie has been successfully added.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add online movie: " + e.getMessage());
            System.out.println("Failed to add online movie: " + e.getMessage());
        }
    }
}