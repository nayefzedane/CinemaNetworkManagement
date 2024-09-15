package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "showtime", nullable = false)
    private LocalDateTime showtime = LocalDateTime.now();  // ברירת מחדל - מועד הקרנה נוכחי

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate = LocalDate.of(2023, 1, 1);  // ברירת מחדל - תאריך יציאה קבוע

    @Column(name = "genre", nullable = false)
    private String genre = "Unspecified";  // ברירת מחדל - ז'אנר לא מוגדר

    @Column(name = "duration", nullable = false)
    private int duration = 120;  // ברירת מחדל - משך סרט 120 דקות

    @Column(name = "rating", nullable = false)
    private float rating = 0.0f;  // ברירת מחדל - דירוג 0

    @Column(name = "director", nullable = false)
    private String director = "Unknown";  // ברירת מחדל - במאי לא ידוע

    @Column(name = "description", nullable = false)
    private String description = "No description available";  // ברירת מחדל - תיאור לא זמין

    @Column(name = "image_path", nullable = false)
    private String imagePath = "/images/background_login.png";  // ברירת מחדל - תמונה דיפולטיבית

    @Column(name = "place", nullable = false)
    private String place = "Default Cinema";  // ברירת מחדל - מיקום קולנוע דיפולטיבי

    @Column(name = "price", nullable = false)
    private float price = 40.0f;  // ברירת מחדל - מחיר כרטיס 40 ש"ח

    @Column(name = "isOnline", nullable = false)
    private boolean isOnline = false;  // ברירת מחדל - אין צפייה ביתית

    @Column(name = "availableSeat", nullable = false)
    private int availableSeat = 100;  // ברירת מחדל - 100 מקומות פנויים

    @Column(name = "hallNumber", nullable = false)
    private int hallNumber = 1;  // ברירת מחדל - אולם מספר 1

    @Lob
    @Column(name = "image_data", columnDefinition="LONGBLOB")
    private byte[] imageData;

    // קונסטרקטור ברירת מחדל
    public Movie() {}

    // קונסטרקטור עם פרמטרים לכל השדות
    public Movie(String title, LocalDateTime showtime, LocalDate releaseDate, String genre, int duration, float rating, String director, String description, String imagePath, String place, float price, boolean isOnline, int availableSeat, int hallNumber) {
        this.title = title;
        this.showtime = showtime;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.director = director;
        this.description = description;
        this.imagePath = imagePath;
        this.place = place;
        this.price = price;
        this.isOnline = isOnline;
        this.availableSeat = availableSeat;
        this.hallNumber = hallNumber;
    }

    public Movie(String title, LocalDateTime showtime, LocalDate releaseDate, String director, String description, float price, boolean isOnline, String genre, int duration, float rating, byte[] imageData) {
        this.title = title;
        this.showtime = showtime;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.director = director;
        this.description = description;
        this.price = price;
        this.isOnline = isOnline;
        this.imageData = imageData;
    }
    public Movie(String title, LocalDateTime showtime, LocalDate releaseDate, String director, String description, float price, boolean isOnline, String genre, int duration, float rating) {
        this.title = title;
        this.showtime = showtime;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.director = director;
        this.description = description;
        this.price = price;
        this.isOnline = isOnline;
    }

    // Getters ו- Setters לכל שדה

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

    public LocalDateTime getShowtime() {
        return showtime;
    }

    public void setShowtime(LocalDateTime showtime) {
        this.showtime = showtime;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public int getAvailableSeat() {
        return availableSeat;
    }

    public void setAvailableSeat(int availableSeat) {
        this.availableSeat = availableSeat;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public void setHallNumber(int hallNumber) {
        this.hallNumber = hallNumber;
    }

    public void setImageData(String base64ImageData) {

        this.imageData = Base64.getDecoder().decode(base64ImageData);
    }
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public String toString() {
        return String.format(
                "id=%d;title=%s;showtime=%s;releaseDate=%s;genre=%s;duration=%d;rating=%.1f;director=%s;description=%s;imagePath=%s;place=%s;price=%.2f;isOnline=%b;availableSeat=%d;hallNumber=%d;imageData=%s",
                this.id, this.title, this.showtime, this.releaseDate, this.genre, this.duration, this.rating, this.director, this.description, this.imagePath, this.place, this.price, this.isOnline, this.availableSeat, this.hallNumber, Base64.getEncoder().encodeToString(this.imageData)
        );
    }
}
