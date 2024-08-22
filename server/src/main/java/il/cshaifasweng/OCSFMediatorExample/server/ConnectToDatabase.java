package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

//this what loay added:
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;
import java.time.LocalDate;
import javax.persistence.criteria.Order;

public class ConnectToDatabase {
    private static SessionFactory sessionFactory;

    private static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Movie.class);
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(purchaseCard.class);
            configuration.addAnnotatedClass(Request.class);
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }

    public static void CreateDatabase() throws HibernateException {
        System.out.println("Data Creation is starting");
        createPurchases();
        createRequests();

        System.out.println("Initial data creation finished");
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // הוספת סרטים לדוגמא עם כל השדות החדשים באמצעות הקונסטרקטור
            Movie movie1 = new Movie("Inception",
                    LocalDateTime.of(2024, 12, 24, 14, 30),  // Showtime
                    LocalDateTime.of(2024, 12, 10, 0, 0),   // Release Date
                    "Sci-Fi", 148, 8.8f, "Christopher Nolan",
                    "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
                    "images/background_login.png", "Cinema City",
                    40.0f, true, 80, 3);
            session.save(movie1);

            Movie movie2 = new Movie("The Shawshank Redemption",
                    LocalDateTime.of(2024, 12, 24, 16, 0),  // Showtime
                    LocalDateTime.of(2024, 12, 5, 0, 0),    // Release Date
                    "Drama", 142, 9.3f, "Frank Darabont",
                    "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                    "images/background_login.png", "Cinema City",
                    35.0f, true, 60, 2);
            session.save(movie2);

            Movie movie3 = new Movie("The Godfather",
                    LocalDateTime.of(2024, 12, 24, 18, 0),  // Showtime
                    LocalDateTime.of(2024, 12, 3, 0, 0),    // Release Date
                    "Crime", 175, 9.2f, "Francis Ford Coppola",
                    "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                    "images/background_login.png", "Yes Planet",
                    50.0f, false, 100, 5);
            session.save(movie3);

            Movie movie4 = new Movie("The Dark Knight",
                    LocalDateTime.of(2024, 12, 24, 20, 0),  // Showtime
                    LocalDateTime.of(2024, 12, 7, 0, 0),    // Release Date
                    "Action", 152, 9.0f, "Christopher Nolan",
                    "When the menace known as the Joker emerges from his mysterious past, he wreaks havoc and chaos on the people of Gotham.",
                    "images/background_login.png", "Yes Planet",
                    45.0f, true, 70, 4);
            session.save(movie4);

            Movie movie5 = new Movie("Pulp Fiction",
                    LocalDateTime.of(2024, 12, 24, 22, 30),  // Showtime
                    LocalDateTime.of(2024, 12, 1, 0, 0),     // Release Date
                    "Crime", 154, 8.9f, "Quentin Tarantino",
                    "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                    "images/background_login.png", "Cinema City",
                    42.0f, true, 85, 2);
            session.save(movie5);

            Movie movie6 = new Movie("Schindler's List",
                    LocalDateTime.of(2024, 12, 24, 10, 30),  // Showtime
                    LocalDateTime.of(2024, 11, 28, 0, 0),    // Release Date
                    "Biography", 195, 8.9f, "Steven Spielberg",
                    "In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.",
                    "images/background_login.png", "Yes Planet",
                    50.0f, false, 60, 1);
            session.save(movie6);

            Movie movie7 = new Movie("Fight Club",
                    LocalDateTime.of(2024, 12, 24, 12, 15),  // Showtime
                    LocalDateTime.of(2024, 12, 2, 0, 0),     // Release Date
                    "Drama", 139, 8.8f, "David Fincher",
                    "An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more.",
                    "images/background_login.png", "Cinema City",
                    38.0f, true, 90, 3);
            session.save(movie7);

            // הוספת משתמשים לדוגמא
            User admin = new User("admin", "admin123", "Admin");
            User manager = new User("manager", "manager123", "Manager");
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

    //adding purchases
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

    public static void createRequests() {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            Request request1 = new Request(
                    "Update Price",
                    "Update Price movie 2 to:50"
            );
            session.save(request1);
            Request request2 = new Request(
                    "Update Price",
                    "Update price movie 15 to:1000"
            );
            session.save(request2);
            Request request3 = new Request(
                    "Update Price",
                    "The Shawshank Redemption, Id: 2, Showtime:2024-12-24T16, Place:00, Old price: 35.0, New price: 60.0"
            );
            session.save(request3);


            session.getTransaction().commit();
            System.out.println("Sample requests created successfully");
        } catch (HibernateException e) {
            System.err.println("Error creating sample requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<purchaseCard> getAllPurchasesOrderedByDate() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<purchaseCard> query = builder.createQuery(purchaseCard.class);
            Root<purchaseCard> root = query.from(purchaseCard.class);

            // Order by purchase date
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

    public static List<Request> getAllPriceChangeRequests() throws Exception {
        System.out.println("we are on data base asking for the requests");
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Request> query = builder.createQuery(Request.class);
            Root<Request> root = query.from(Request.class);

            // Select all requests
            query.select(root);

            List<Request> requests = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return requests;
        } catch (Exception e) {
            System.err.println("Error fetching requests: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void deleteRequestById(Long id) {
        System.out.println("we are on connect to data base and we are deleting a request");
        System.out.println(id);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Request request = session.get(Request.class, id);
        if (request != null) {
            session.delete(request);
        } else {
            System.out.println("Request with ID " + id + " not found.");
        }
        session.getTransaction().commit();
        session.close();
    }

    public static void updateMoviePrice(int movieId, float newprice) {
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
            movie.setPrice(newprice);
            session.update(movie);
            transaction.commit();
        }catch (Exception e){
            System.out.println("haha");
            e.printStackTrace();
        }



    }
}



