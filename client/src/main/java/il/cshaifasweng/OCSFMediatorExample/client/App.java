package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class App extends Application {

    private static Scene scene;
    private static Stack<String> history = new Stack<>(); // Stack for screen history

    public static SimpleClient client;

    @Override
    public void start(Stage stage) throws IOException {
        EventBus.getDefault().register(this);

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
        System.out.println("Navigating to: " + fxml); // Debugging output
        scene.setRoot(loadFXML(fxml));
    }

    static void goBack() throws IOException {
        if (history.size() > 1) {
            history.pop(); // Remove current screen from history
            String previousScreen = history.peek(); // Get the previous screen
            System.out.println("Going back to: " + previousScreen); // Debugging output
            scene.setRoot(loadFXML(previousScreen)); // Show the previous screen
        } else {
            System.out.println("No previous screen to go back to."); // Debugging output
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        EventBus.getDefault().unregister(this);
        super.stop();
    }

    @Subscribe
    public void onWarningEvent(WarningEvent event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING,
                    String.format("Message: %s\nTimestamp: %s\n",
                            event.getWarning().getMessage(),
                            event.getWarning().getTime().toString())
            );
            alert.show();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
