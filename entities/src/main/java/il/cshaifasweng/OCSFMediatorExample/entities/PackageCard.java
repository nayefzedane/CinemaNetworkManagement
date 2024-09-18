package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "packages")
public class PackageCard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private int packageId;

    @Column(name = "remaining_entries", nullable = false)
    private int remainingEntries = 20;  // Initially set to 20

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "customer_name", nullable = false)
    private String name = "no name yet";

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "customer_id", nullable = false)
    private int customerId;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "payment_last_four_digits", nullable = false)
    private int paymentLastFourDigits;

    // Default constructor
    public PackageCard() {}

    // Constructor with parameters
    public PackageCard(LocalDate purchaseDate, double price, int customerId,
                       String customerEmail, int paymentLastFourDigits, String name) {
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.paymentLastFourDigits = paymentLastFourDigits;
    }
    public PackageCard(LocalDate purchaseDate, double price, int customerId,
                       String customerEmail, int paymentLastFourDigits) {
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.paymentLastFourDigits = paymentLastFourDigits;
    }

    // Getters and setters
    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getRemainingEntries() {
        return remainingEntries;
    }

    public void setRemainingEntries(int remainingEntries) {
        this.remainingEntries = remainingEntries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public int getPaymentLastFourDigits() {
        return paymentLastFourDigits;
    }

    public void setPaymentLastFourDigits(int paymentLastFourDigits) {
        this.paymentLastFourDigits = paymentLastFourDigits;
    }

    // Method to use the package, decreasing the remaining entries by one if possible
    public boolean usePackage() {
        if (remainingEntries > 0) {
            remainingEntries--;
            return true;
        } else {
            return false;
        }
    }


}
