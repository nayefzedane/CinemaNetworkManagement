package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MoviesTable {
    @FXML
    void GetAllMovies(ActionEvent event) throws IOException {
        SimpleClient.getClient().sendToServer("get all movies");
    }


    }

