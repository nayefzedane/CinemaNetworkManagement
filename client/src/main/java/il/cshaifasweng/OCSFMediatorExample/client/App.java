package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class App extends Application {

    private static Scene scene;
    private static Stack<String> history = new Stack<>(); // Stack for screen history

    public static SimpleClient client;

    @Override
    public void start(Stage stage) throws IOException {
        // Start with the connection window
        scene = new Scene(loadFXML("InitConnection"), 640, 480);
        stage.setScene(scene);
        stage.show();
        history.push("InitConnection"); // Add the initial screen to history
    }

    static void setRoot(String fxml) throws IOException {
        if (!history.isEmpty() && !history.peek().equals(fxml)) {
            history.push(fxml); // Add current screen to history
        }
        scene.setRoot(loadFXML(fxml));
    }

    static Object getController() {
        return scene.getRoot().getUserData();
    }

    static void goBack() throws IOException {
        if (history.size() > 1) {
            history.pop(); // Remove current screen from history
            String previousScreen = history.peek(); // Get the previous screen
            scene.setRoot(loadFXML(previousScreen)); // Show the previous screen
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        root.setUserData(fxmlLoader.getController());
        return root;
    }

    public static void main(String[] args) {
        launch();
    }
}
