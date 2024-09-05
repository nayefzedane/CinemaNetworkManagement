package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "requests")
public class Request implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    public Request() {
    }

    public Request(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Request{" + "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' + '}';
    }
}

