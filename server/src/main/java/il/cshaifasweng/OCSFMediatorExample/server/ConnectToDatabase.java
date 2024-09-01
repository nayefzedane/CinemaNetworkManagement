package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;

public class ConnectToDatabase {
    private static SessionFactory sessionFactory;

    private static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Movie.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(purchaseCard.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }

    public static void CreateDatabase() throws HibernateException {
        System.out.println("Data Creation is starting");
        createPurchases();
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // הוספת סרטים לדוגמא עם כל השדות החדשים באמצעות הקונסטרקטור
            Movie movie1 = new Movie("Inception",
                    LocalDateTime.of(2024, 12, 24, 14, 30),  // Showtime
                    LocalDate.of(2024, 12, 10),   // Release Date
                    "Sci-Fi", 148, 8.8f, "Christopher Nolan",
                    "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
                    "images/background_login.png", "Cinema City",
                    40.0f, false, 80, 3);
            session.save(movie1);

            Movie movie2 = new Movie("The Shawshank Redemption",
                    LocalDateTime.of(2024, 12, 24, 16, 0),  // Showtime
                    LocalDate.of(2024, 12, 5),    // Release Date
                    "Drama", 142, 9.3f, "Frank Darabont",
                    "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                    "images/background_login.png", "Cinema City",
                    35.0f, true, 60, 2);
            session.save(movie2);

            Movie movie3 = new Movie("The Godfather",
                    LocalDateTime.of(2024, 12, 24, 18, 0),  // Showtime
                    LocalDate.of(2024, 12, 3),    // Release Date
                    "Crime", 175, 9.2f, "Francis Ford Coppola",
                    "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                    "images/background_login.png", "Yes Planet",
                    50.0f, false, 100, 5);
            session.save(movie3);

            Movie movie4 = new Movie("The Dark Knight",
                    LocalDateTime.of(2024, 12, 24, 20, 0),  // Showtime
                    LocalDate.of(2024, 12, 7),    // Release Date
                    "Action", 152, 9.0f, "Christopher Nolan",
                    "When the menace known as the Joker emerges from his mysterious past, he wreaks havoc and chaos on the people of Gotham.",
                    "images/background_login.png", "Yes Planet",
                    45.0f, true, 70, 4);
            session.save(movie4);

            Movie movie5 = new Movie("Pulp Fiction",
                    LocalDateTime.of(2024, 12, 24, 22, 30),  // Showtime
                    LocalDate.of(2024, 12, 1),     // Release Date
                    "Crime", 154, 8.9f, "Quentin Tarantino",
                    "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                    "images/background_login.png", "Cinema City",
                    42.0f, true, 85, 2);
            session.save(movie5);

            Movie movie6 = new Movie("Schindler's List",
                    LocalDateTime.of(2024, 12, 24, 10, 30),  // Showtime
                    LocalDate.of(2024, 11, 28),    // Release Date
                    "Biography", 195, 8.9f, "Steven Spielberg",
                    "In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.",
                    "images/background_login.png", "Yes Planet",
                    50.0f, false, 60, 1);
            session.save(movie6);

            Movie movie7 = new Movie("Fight Club",
                    LocalDateTime.of(2024, 12, 24, 12, 15),  // Showtime
                    LocalDate.of(2024, 12, 2),     // Release Date
                    "Drama", 139, 8.8f, "David Fincher",
                    "An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more.",
                    "images/background_login.png", "Cinema City",
                    38.0f, true, 90, 3);
            session.save(movie7);

            // הוספת משתמשים לדוגמא
            User admin = new User("admin", "admin123", "Admin");
            User manager = new User("man", "123", "Manager");
            User customer = new User("customer", "customer123", "Customer");
            User customerservice = new User("customerservice", "customerservice123", "CustomerService");
            session.save(admin);
            session.save(manager);
            session.save(customer);
            session.save(customerservice);

            session.getTransaction().commit();
            System.out.println("Initial data creation finished");
        } catch (HibernateException e) {
            System.err.println("Error during initial data creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateShowtime(String title, LocalDateTime newShowtime) throws Exception {
        System.out.println("Update function reached...");
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);
            query.select(root).where(builder.equal(root.get("title"), title));

            List<Movie> movies = session.createQuery(query).getResultList();

            if (movies.isEmpty()) {
                System.out.println("Movie with title \"" + title + "\" not found.");
                session.getTransaction().rollback();
                return;
            }

            Movie temp = movies.get(0);
            temp.setShowtime(newShowtime);
            session.update(temp);
            session.getTransaction().commit();

            System.out.println("Updated showtime for movie: " + title);
        } catch (Exception e) {
            System.out.println("Error updating showtime: " + e.getMessage());
            throw e;
        }
    }

    public static Session initializeDatabase() throws IOException {
        try {
            Session session = getSessionFactory().openSession();
            session.beginTransaction();
            session.clear();
            CreateDatabase();
            session.getTransaction().commit();
            session.close();
            return session;
        } catch (Exception exception) {
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
            throw new IOException("Database initialization failed.", exception);
        }
    }

    public static List<Movie> getAllMovies() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);
            query.select(root);

            List<Movie> data = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return data;
        } catch (Exception e) {
            System.err.println("Error fetching movies: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static User getUserByCredentials(String username, String password) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(builder.equal(root.get("username"), username), builder.equal(root.get("password"), password));

            User user = session.createQuery(query).uniqueResult();
            session.getTransaction().commit();

            return user;
        } catch (Exception e) {
            System.err.println("Error fetching user by credentials: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void addMovie(Movie movie) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(movie);  // Save the movie object to the database
            transaction.commit();
            System.out.println("Movie added successfully: " + movie.getTitle());
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();  // Rollback in case of an error
            }
            System.err.println("Failed to add movie: " + movie.getTitle());
            e.printStackTrace();  // Log the exception for debugging purposes
        }
    }

    public static Long getMovieCountFromDatabase() {
        try {
            Session session = getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            Long count = session.createQuery("SELECT COUNT(m) FROM Movie m", Long.class).getSingleResult();
            tx.commit();
            session.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteMovieById(int id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Movie movie = session.get(Movie.class, id);
        if (movie != null) {
            session.delete(movie);
        } else {
            System.out.println("Movie with ID " + id + " not found.");
        }
        session.getTransaction().commit();
        session.close();
    }
    // הוספת הפונקציה ליצירת רשומות purchaseCard
    public static void createPurchases() {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            purchaseCard purchase1 = new purchaseCard(
                    LocalDate.of(2024, 8, 17),
                    "Cinema City",
                    40,
                    1001,
                    1234,
                    "Inception",
                    LocalDateTime.of(2024, 12, 24, 14, 30)
            );
            session.save(purchase1);
            purchaseCard purchase2 = new purchaseCard(
                    LocalDate.of(2024, 8, 17),
                    "Yes Planet",
                    45,
                    1002,
                    5678,
                    "The Dark Knight",
                    LocalDateTime.of(2024, 12, 24, 20, 0)
            );
            session.save(purchase2);

            purchaseCard purchase3 = new purchaseCard(
                    LocalDate.of(2024, 3, 17),
                    "Yes Planet",
                    100,
                    1003,
                    1414,
                    "loay asaad",
                    LocalDateTime.of(2025, 12, 24, 20, 0)
            );
            session.save(purchase3);

            session.getTransaction().commit();
            System.out.println("Sample purchases created successfully");
        } catch (HibernateException e) {
            System.err.println("Error creating sample purchases: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // הוספת הפונקציה להחזרת כל רכישות
    public static List<purchaseCard> getAllPurchasesOrderedByDate() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<purchaseCard> query = builder.createQuery(purchaseCard.class);
            Root<purchaseCard> root = query.from(purchaseCard.class);

            // סדר לפי תאריך הרכישה
            query.select(root).orderBy(builder.asc(root.get("purchaseDate")));

            List<purchaseCard> purchases = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return purchases;
        } catch (Exception e) {
            System.err.println("Error fetching purchases: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void updateMovieShowtimeInDatabase(int movieId, LocalDateTime newShowtime) {
        Session session = ConnectToDatabase.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Retrieve the movie from the database
            Movie movie = session.get(Movie.class, movieId);
            if (movie == null) {
                throw new Exception("Movie not found.");
            }

            // Update the showtime
            movie.setShowtime(newShowtime);
            session.update(movie);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        System.out.println("Show time updated successfully");
    }
    public static List<Movie> getMoviesByOnlineStatus(boolean isOnline) {
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);
            query.select(root).where(builder.equal(root.get("isOnline"), isOnline));
            return session.createQuery(query).getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }
//    public static List<Movie> searchMoviesByAdvancedCriteria(String cinema, LocalDate startDate, LocalDate endDate, String genre, String title, boolean isOnline) {
//        try (Session session = getSessionFactory().openSession()) {
//            session.beginTransaction();
//
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
//            Root<Movie> root = query.from(Movie.class);
//
//            // התחלת הבנייה של השאילתה
//            query.select(root);
//
//            // הוספת קריטריון חובה - סינון לפי אונליין או לא
//            query.where(builder.equal(root.get("isOnline"), isOnline));
//
//            // הוספת תנאים נוספים לפי הקריטריונים שנבחרו
//            if (cinema != null && !cinema.isEmpty()) {
//                query.where(builder.equal(root.get("place"), cinema));
//            }
//            if (startDate != null && endDate != null) {
//                query.where(builder.between(root.get("showtime").as(LocalDate.class), startDate, endDate));
//            }
//            if (genre != null && !genre.isEmpty()) {
//                query.where(builder.equal(root.get("genre"), genre));
//            }
//            if (title != null && !title.isEmpty()) {
//                query.where(builder.like(root.get("title"), "%" + title + "%"));
//            }
//
//            List<Movie> movies = session.createQuery(query).getResultList();
//            session.getTransaction().commit();
//
//            return movies;
//        } catch (Exception e) {
//            System.err.println("Error fetching movies by advanced criteria: " + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static List<Movie> searchMoviesByAdvancedCriteria(String cinema, LocalDate startDate, LocalDate endDate, String genre, String title, boolean isOnline) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);

            // התחלת הבנייה של השאילתה
            query.select(root);

            // הוספת קריטריון חובה - סינון לפי אונליין או לא
            query.where(builder.equal(root.get("isOnline"), isOnline));

            // הוספת תנאים נוספים לפי הקריטריונים שנבחרו
            if (cinema != null && !cinema.isEmpty() && !"ALL".equals(cinema)) {
                query.where(
                        builder.and(
                                builder.equal(root.get("isOnline"), isOnline),
                                builder.equal(root.get("place"), cinema)
                        )
                );
            }

            if (startDate != null && endDate != null) {
                query.where(
                        builder.and(
                                builder.equal(root.get("isOnline"), isOnline),
                                builder.between(root.get("showtime").as(LocalDate.class), startDate, endDate)
                        )
                );
            }

            if (genre != null && !genre.isEmpty() && !"ALL".equals(genre)) {
                query.where(
                        builder.and(
                                builder.equal(root.get("isOnline"), isOnline),
                                builder.equal(root.get("genre"), genre)
                        )
                );
            }

            if (title != null && !title.isEmpty()) {
                query.where(
                        builder.and(
                                builder.equal(root.get("isOnline"), isOnline),
                                builder.like(root.get("title"), "%" + title + "%")
                        )
                );
            }

            List<Movie> movies = session.createQuery(query).getResultList();
            session.getTransaction().commit();

            return movies;
        } catch (Exception e) {
            System.err.println("Error fetching movies by advanced criteria: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

//    public static List<Movie> searchMoviesByAdvancedCriteria(String cinema, LocalDate startDate, LocalDate endDate, String genre, String title) {
//        try (Session session = getSessionFactory().openSession()) {
//            session.beginTransaction();
//
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
//            Root<Movie> root = query.from(Movie.class);
//
//            // התחלת הבנייה של השאילתה
//            query.select(root);
//
//            // הוספת תנאים לפי הקריטריונים
//            if (cinema != null && !cinema.isEmpty()) {
//                query.where(builder.equal(root.get("place"), cinema));
//            }
//            if (startDate != null && endDate != null) {
//                query.where(
//                        builder.between(root.get("showtime").as(LocalDate.class), startDate, endDate)
//                );
//            }
//            if (genre != null && !genre.isEmpty()) {
//                query.where(builder.equal(root.get("genre"), genre));
//            }
//            if (title != null && !title.isEmpty()) {
//                query.where(builder.like(root.get("title"), "%" + title + "%"));
//            }
//
//            List<Movie> movies = session.createQuery(query).getResultList();
//            session.getTransaction().commit();
//
//            return movies;
//        } catch (Exception e) {
//            System.err.println("Error fetching movies by advanced criteria: " + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }
    public static List<Movie> searchOnlineMoviesByCriteria(String genre, String title) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);

            query.select(root).where(builder.equal(root.get("isOnline"), true));

            // הוספת תנאים לפי הקריטריונים
            if (genre != null && !genre.isEmpty()) {
                query.where(builder.and(
                        builder.equal(root.get("genre"), genre),
                        builder.equal(root.get("isOnline"), true)
                ));
            }
            if (title != null && !title.isEmpty()) {
                query.where(builder.and(
                        builder.like(root.get("title"), "%" + title + "%"),
                        builder.equal(root.get("isOnline"), true)
                ));
            }

            List<Movie> movies = session.createQuery(query).getResultList();
            session.getTransaction().commit();

            return movies;
        } catch (Exception e) {
            System.err.println("Error fetching online movies by criteria: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public static List<Movie> getMoviesByScreeningDate(LocalDate startDate, LocalDate endDate) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);

            // Filtering movies based on screening dates
            if (startDate != null && endDate != null) {
                query.select(root).where(builder.between(root.get("showtime").as(LocalDate.class), startDate, endDate));
            } else if (startDate != null) {
                query.select(root).where(builder.greaterThanOrEqualTo(root.get("showtime").as(LocalDate.class), startDate));
            } else if (endDate != null) {
                query.select(root).where(builder.lessThanOrEqualTo(root.get("showtime").as(LocalDate.class), endDate));
            } else {
                query.select(root); // If no date criteria are provided, return all movies
            }

            List<Movie> movies = session.createQuery(query).getResultList();
            session.getTransaction().commit();

            return movies;
        } catch (Exception e) {
            System.err.println("Error fetching movies by screening date: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}