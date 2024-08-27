package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML
    private Button menuButton;

    @FXML
    private GridPane contentGrid;

    @FXML
    private VBox sidebar; // התפריט הצדדי

    @FXML
    private Button goToLoginButton;

    @FXML
    private Button goToHomeButton;

    @FXML
    private HBox closeToWatchingBox; // בלוק הסרטים הקרובים לצפייה

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (contentGrid != null) {  // בדיקה שה-GridPane מאופס כראוי
            contentGrid.sceneProperty().addListener(new ChangeListener<Scene>() {
                @Override
                public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                    if (newScene != null) {
                        newScene.getStylesheets().add(getClass().getResource("customer_dashboard.css").toExternalForm());
                    }
                }
            });
        } else {
            System.err.println("Error: contentGrid is null. Please check your FXML file.");
        }

        // בדיקה שה-sidebar אותחל כראוי
        if (sidebar == null) {
            System.out.println("Error: Sidebar is null. Please check your FXML file.");
        }

        // הוספת כפתור לכניסה לחלון הבית (הלקוח הנוכחי)
        goToHomeButton.setOnAction(e -> goToHome());

        // הוספת כפתור לכניסה לחלון הכניסה (Login)
        goToLoginButton.setOnAction(e -> goToLogin());

        // שליחת בקשה לטעינת הסרטים מהשרת
        loadMovies();
    }

    @FXML
    public void toggleMenu() {
        if (sidebar != null) {
            sidebar.setVisible(!sidebar.isVisible());
        } else {
            System.out.println("Error: Sidebar is null. Cannot toggle visibility.");
        }
    }

    @FXML
    public void goToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToHome() {
        try {
            App.setRoot("customer_dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMovies() {
        try {
            // שליחת בקשה לשרת לקבלת כל הסרטים
            SimpleClient.getClient().sendToServer("get all movies");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayMovies(List<Movie> movies) {
        Platform.runLater(() -> {
            int col = 0, row = 0;
            contentGrid.getChildren().clear(); // נקה את התוכן הנוכחי
            closeToWatchingBox.getChildren().clear(); // נקה את הבלוק של הסרטים הקרובים לצפייה

            // קבלת גודל המסך
            double screenWidth = contentGrid.getScene().getWindow().getWidth();
            double screenHeight = contentGrid.getScene().getWindow().getHeight();

            // שינוי דינמי של גודל התמונה והכפתור בהתאם לגודל המסך
            double imageWidth = screenWidth / 5;
            double imageHeight = screenHeight / 4;

            // הוספת הסרטים הקרובים לצפייה לבלוק העליון
            for (int i = 0; i < Math.min(5, movies.size()); i++) {
                Movie movie = movies.get(i);
                ImageView imageView = new ImageView(new Image(movie.getImagePath()));
                imageView.setFitHeight(imageHeight);
                imageView.setFitWidth(imageWidth);
                imageView.getStyleClass().add("image-view");

                closeToWatchingBox.getChildren().add(imageView);
            }

            // הוספת שאר הסרטים לרשת התוכן
            for (Movie movie : movies) {
                ImageView imageView = new ImageView(new Image(movie.getImagePath()));
                imageView.setFitHeight(imageHeight);
                imageView.setFitWidth(imageWidth);
                imageView.getStyleClass().add("image-view");

                Button button = new Button("More Info");
                button.setOnAction(e -> showMovieDetails(movie));
                button.setPrefWidth(imageWidth);
                button.setPrefHeight(50);

                contentGrid.add(imageView, col, row);
                contentGrid.add(button, col, row + 1);

                col++;
                if (col == 4) { // במידה ורוצים 4 סרטים בכל שורה
                    col = 0;
                    row += 2;
                }
            }
        });
    }

    private void showMovieDetails(Movie movie) {
        // כאן תוכל להציג פרטים נוספים על הסרט בחלון חדש או פופאפ
        System.out.println("Showing details for: " + movie.getTitle());
    }
}
