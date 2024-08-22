package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class purchaseCard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "customer_id", nullable = false)
    private int customerId;

    @Column(name = "payment_card_last_four", nullable = false)
    private int paymentCardLastFour;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "show_time", nullable = false)
    private LocalDateTime showTime;

    // Default constructor
    public purchaseCard() {}

    // Constructor with parameters
    public purchaseCard(LocalDate purchaseDate, String branchName, float price, int customerId,
                        int paymentCardLastFour, String movieTitle, LocalDateTime showTime) {
        this.purchaseDate = purchaseDate;
        this.branchName = branchName;
        this.price = price;
        this.customerId = customerId;
        this.paymentCardLastFour = paymentCardLastFour;
        this.movieTitle = movieTitle;
        this.showTime = showTime;
    }





    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPaymentCardLastFour() {
        return paymentCardLastFour;
    }

    public void setPaymentCardLastFour(int paymentCardLastFour) {
        this.paymentCardLastFour = paymentCardLastFour;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    @Override
    public String toString() {
        return String.format("Purchase Order ID: %d\nPurchase Date: %s\nBranch Name: %s\nPrice: %.2f\nCustomer ID: %d\nPayment Card Last Four: %d\nMovie Title: %s\nShow Time: %s",
                this.orderId, this.purchaseDate, this.branchName, this.price, this.customerId, this.paymentCardLastFour, this.movieTitle, this.showTime);
    }
}