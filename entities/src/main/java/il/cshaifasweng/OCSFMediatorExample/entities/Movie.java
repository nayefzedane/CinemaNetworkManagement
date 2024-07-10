package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "release_date")
    private LocalTime releaseDate;

    @Column(name = "genre")
    private String genre;

    @Column(name = "duration")
    private int duration; // in minutes

    @Column(name = "rating")
    private float rating;

    @Column(name = "director")
    private String director;

    @Column(name = "description")
    private String description;



    public Movie() {
        // Default constructor
    }

    public Movie(String title, LocalTime releaseDate, String genre, int duration, float rating, String director, String description) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.director = director;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return String.format("Movie ID: %d\nTitle: %s\nRelease Date: %s\nGenre: %s\nDuration: %d minutes\nRating: %.1f\nDirector: %s\nDescription: %s",
                this.id, this.title, this.releaseDate, this.genre, this.duration, this.rating, this.director, this.description);
}
}
