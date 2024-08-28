package il.cshaifasweng.OCSFMediatorExample.entities;
import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaints implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "mail", nullable = false)
    private String mail;

    @Column(name = "branch", nullable = false)
    private String branch = "Branch";  // Default value

    @Column(name = "complain_title", nullable = false)
    private String complainTitle;

    @Column(name = "complain_text", nullable = false)
    private String complainText;

    @Column(name = "answer")
    private String answer = "";  // Default value

    @Column(name = "financial_compensation", nullable = false)
    private int financialCompensation = 0;  // Default value

    @Column(name = "complain_date", nullable = false)
    private LocalDateTime complainDate;

    // Default constructor
    public Complaints() {}

    // Constructor with parameters (except answer and financialCompensation which have default values)
    public Complaints(String mail, String branch, String complainTitle, String complainText, LocalDateTime complainDate) {
        this.mail = mail;
        this.branch = (branch != null && !branch.isEmpty()) ? branch : "Branch";  // Use default if not provided
        this.complainTitle = complainTitle;
        this.complainText = complainText;
        this.complainDate = complainDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getComplainTitle() {
        return complainTitle;
    }

    public void setComplainTitle(String complainTitle) {
        this.complainTitle = complainTitle;
    }

    public String getComplainText() {
        return complainText;
    }

    public void setComplainText(String complainText) {
        this.complainText = complainText;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getFinancialCompensation() {
        return financialCompensation;
    }

    public void setFinancialCompensation(int financialCompensation) {
        this.financialCompensation = financialCompensation;
    }

    public LocalDateTime getComplainDate() {
        return complainDate;
    }

    public void setComplainDate(LocalDateTime complainDate) {
        this.complainDate = complainDate;
    }

    @Override
    public String toString() {
        return "Complaints{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                ", branch='" + branch + '\'' +
                ", complainTitle='" + complainTitle + '\'' +
                ", complainText='" + complainText + '\'' +
                ", answer='" + answer + '\'' +
                ", financialCompensation=" + financialCompensation +
                ", complainDate=" + complainDate +
                '}';
    }
}
