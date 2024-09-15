package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaymentController {

    @FXML
    private TextField seatField;

    @FXML
    private Label movieLabel; // מציג את שם הסרט
    @FXML
    private Label cinemaLabel; // מציג את שם הסינמה

    @FXML
    private Button closeButton; // כפתור סגירת החלון

    private Stage paymentStage;

    // פונקציה שמקבלת Stage לחזור אליו
    public void setPaymentStage(Stage paymentStage) {
        this.paymentStage = paymentStage;
    }

    // פונקציה שמקבלת את פרטי הסרט ומציגה אותם בחלון
    public void setMovieDetails(Movie movie) {
        movieLabel.setText("Movie: " + movie.getTitle());
        cinemaLabel.setText("Cinema: " + movie.getPlace());
    }

    // פונקציה לטיפול בתשלום
    @FXML
    public void handlePayment() {
        String seat = seatField.getText();
        if (seat.isEmpty()) {
            System.out.println("Please enter a seat number.");
        } else {
            System.out.println("Processing payment for seat: " + seat);
            paymentStage.close(); // סגירת החלון לאחר התשלום
        }
    }

    // פונקציה לסגירת החלון
    @FXML
    public void closeWindow() {
        paymentStage.close(); // סוגר את חלון התשלום
    }
}
