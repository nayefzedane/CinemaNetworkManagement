package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "purchase_links")
public class PurchaseLink implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private int linkId;

    @Column(name = "purchase_time", nullable = false)
    private LocalDateTime purchaseTime;

    @Column(name = "customer_id", nullable = false)
    private int customerId;

    @Column(name = "payment_card_last_four", nullable = false)
    private int paymentCardLastFour;

    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "available_from", nullable = false)
    private LocalDateTime availableFrom;

    @Column(name = "customer_mail", nullable = false)
    private String customerMail;

    @Column(name = "available", nullable = false)
    private boolean available;

    @Column(name = "unique_link", nullable = false)
    private String uniqueLink;
    @Column(name = "price", nullable = false)
    private float price;

    // Default constructor
    public PurchaseLink() {}

    // Constructor with parameters
    public PurchaseLink(LocalDateTime purchaseTime, int customerId, int paymentCardLastFour, String movieTitle, String customerMail, float price) {
        this.purchaseTime = purchaseTime;
        this.customerId = customerId;
        this.paymentCardLastFour = paymentCardLastFour;
        this.movieTitle = movieTitle;
        this.customerMail = customerMail;
        this.availableFrom = purchaseTime.plusHours(3);  // Automatically set availableFrom to purchaseTime + 3 hours
        this.available = false; // Initially false, it will be true after 3 hours
        this.uniqueLink = generateUniqueLink();
        this.price = price;
        // Generate the unique link when the object is created
    }

    // Method to generate a unique link
    private String generateUniqueLink() {
        String cinemaName = "cinemaName";  // Replace with the actual cinema name
        String randomCode = generateRandomCode(5);  // Generate a random alphanumeric code of length 5
        return "www." + cinemaName + ".com/" + randomCode;
    }

    // Helper method to generate a random alphanumeric code of a given length
    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    // Getters and setters
    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(LocalDateTime purchaseTime) {
        this.purchaseTime = purchaseTime;
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

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getCustomerMail() {
        return customerMail;
    }

    public void setCustomerMail(String customerMail) {
        this.customerMail = customerMail;
    }

    public boolean isAvailable() {
        if (LocalDateTime.now().isBefore(availableFrom)) {
            return false; // The link isn't available yet
        }
        LocalDateTime expirationTime = availableFrom.plusDays(2);
        return LocalDateTime.now().isBefore(expirationTime);
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getUniqueLink() {
        return uniqueLink;
    }

    public void setUniqueLink(String uniqueLink) {
        this.uniqueLink = uniqueLink;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
