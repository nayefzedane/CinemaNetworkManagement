package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.User;

import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.PurchaseLink;
import il.cshaifasweng.OCSFMediatorExample.entities.PackageCard;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaints;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import java.time.LocalDateTime;
import org.hibernate.*;





import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import il.cshaifasweng.OCSFMediatorExample.entities.purchaseCard;

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
            configuration.addAnnotatedClass(PurchaseLink.class);
            configuration.addAnnotatedClass(PackageCard.class);
            configuration.addAnnotatedClass(Complaints.class);



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
        createPurchaseLinks();
        createPackageCards();
        createComplaints();

        System.out.println("Initial data creation finished");

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
            User adminhaifa = new User("adminha", "123", "admin haifa");
            User adminnazareth = new User("adminna", "123", "admin nazareth");
            session.save(admin);
            session.save(manager);
            session.save(customer);
            session.save(customerservice);
            session.save(adminhaifa);
            session.save(adminnazareth);

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

    public static void addMovie(Movie movie) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(movie);  // Save the movie object to the database
            transaction.commit();
            System.out.println("Movie added successfully: " + movie.getTitle());
            try {
                String movie_title = movie.getTitle();
                LocalDateTime time = movie.getShowtime();
                String branch = movie.getPlace();

                List<PackageCard> packages = getAllPackageCardsOrderedByDate();
                for (PackageCard packageCard : packages) {
                    //
                    String cost_mail = packageCard.getCustomerEmail(); //
                    String name = packageCard.getName();
                    EmailService.sendEmail(cost_mail, "Announcement of a new movie", "Hello " + name + " from Cinema City!\n We are happy to tell you about a new movie upcomming\n" +movie_title +"\n" + "is available on " + time +" in our " + branch + " branch.\nDont waste it.");



                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
                    LocalDateTime.of(2024, 12, 24, 14, 30),
                    "loay@gmail.com"
            );
            session.save(purchase1);
            purchaseCard purchase2 = new purchaseCard(
                    LocalDate.of(2024, 8, 17),
                    "Yes Planet",
                    45,
                    1002,
                    5678,
                    "The Dark Knight",
                    LocalDateTime.of(2024, 12, 24, 20, 0),
                    "mohammed@gmail.com"
            );
            session.save(purchase2);

            purchaseCard purchase3 = new purchaseCard(
                    LocalDate.of(2024, 3, 17),
                    "Yes Planet",
                    100,
                    1003,
                    1414,
                    "loay asaad",
                    LocalDateTime.of(2025, 12, 24, 20, 0),
                    "nayef@gmail.com"
            );
            session.save(purchase3);
            purchaseCard purchase4 = new purchaseCard(
                    LocalDate.of(2024, 8, 1),
                    "Yes Planet",
                    60,
                    1004,
                    1414,
                    "nayef zedan",
                    LocalDateTime.of(2025, 12, 24, 20, 0),
                    "nayef1@gmail.com"
            );
            session.save(purchase4);
            purchaseCard purchase5 = new purchaseCard(
                    LocalDate.of(2024, 8, 1),
                    "Cinema City",
                    25,
                    1006,
                    1414,
                    "real madrid",
                    LocalDateTime.of(2024, 9, 6, 1, 0),
                    "madrid@gmail.com"
            );
            session.save(purchase5);

            session.getTransaction().commit();
            System.out.println("Sample purchases created successfully");

        } catch (HibernateException e) {
            System.err.println("Error creating sample purchases: " + e.getMessage());
            e.printStackTrace();
        }
    }

   

    public static void createPurchaseLinks() {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // Creating a sample PurchaseLink
            PurchaseLink purchaseLink1 = new PurchaseLink(
                    LocalDateTime.of(2024, 8, 17, 10, 0),  // purchaseTime
                    1004,  // customerId
                    9876,  // paymentCardLastFour
                    "Avatar",  // movieTitle
                    "example@example.com",  // customerMail
                    50
            );
            session.save(purchaseLink1);

            // You can add more PurchaseLink samples as needed
            PurchaseLink purchaseLink2 = new PurchaseLink(
                    LocalDateTime.of(2024, 8, 18, 15, 0),  // purchaseTime
                    1005,  // customerId
                    4321,  // paymentCardLastFour
                    "Interstellar",  // movieTitle
                    "sample@example.com",
                    30// customerMail
            );
            session.save(purchaseLink2);
            PurchaseLink purchaseLink3 = new PurchaseLink(
                    LocalDateTime.of(2024, 8, 18, 15, 0),  // purchaseTime
                    1005,  // customerId
                    4321,  // paymentCardLastFour
                    "Red Dead Redemption 2",  // movieTitle
                    "sample@example.com",
                    30,"www.loay.com"// customerMail
            );
            session.save(purchaseLink3);

            session.getTransaction().commit();
            System.out.println("Sample purchase links created successfully");

        } catch (HibernateException e) {
            System.err.println("Error creating sample purchase links: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void createPackageCards() {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // Creating a sample PackageCard
            PackageCard packageCard1 = new PackageCard(
                    LocalDate.of(2024, 8, 17),  // purchaseDate
                    100.00,  // price
                    1004,  // customerId
                    "example@example.com",  // customerEmail
                    9876  // paymentLastFourDigits
            );
            session.save(packageCard1);

            // Adding another sample PackageCard
            PackageCard packageCard2 = new PackageCard(
                    LocalDate.of(2024, 8, 18),  // purchaseDate
                    120.00,  // price
                    1005,  // customerId
                    "sample@example.com",  // customerEmail
                    4321  // paymentLastFourDigits
            );
            session.save(packageCard2);

            session.getTransaction().commit();
            System.out.println("Sample package cards created successfully");

        } catch (HibernateException e) {
            System.err.println("Error creating sample package cards: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void createComplaints() {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // Creating sample complaints
            Complaints complaint1 = new Complaints(
                    "user1@example.com",
                    "Cinema City",
                    "Long Queue",
                    "The queue was too long and slow.",
                    LocalDateTime.of(2024, 8, 15, 10, 30)
            );
            session.save(complaint1);

            Complaints complaint2 = new Complaints(
                    "user2@example.com",
                    "Yes Planet",
                    "Uncomfortable Seats",
                    "The seats were very uncomfortable.",
                    LocalDateTime.of(2024, 8, 16, 14, 45)
            );
            session.save(complaint2);

            Complaints complaint3 = new Complaints(
                    "user3@example.com",
                    "",  // No branch provided, should use default value
                    "Late Start",
                    "The movie started 15 minutes late.",
                    LocalDateTime.of(2024, 8, 17, 18, 00)
            );
            session.save(complaint3);

            Complaints complaint4 = new Complaints(
                    "user4@example.com",
                    "Cinema City",
                    "Poor Sound Quality",
                    "The sound quality was poor and distorted.",
                    LocalDateTime.of(2024, 8, 17, 20, 15)
            );
            session.save(complaint4);

            Complaints complaint5 = new Complaints(
                    "user5@example.com",
                    "Yes Planet",
                    "Rude Staff",
                    "The staff was rude and unhelpful.",
                    LocalDateTime.of(2024, 8, 2, 11, 20)
            );
            session.save(complaint5);
            Complaints complaint6 = new Complaints(
                    "user5@example.com",
                    "Yes Planet",
                    "Rude Staff",
                    "The staff was rude and unhelpful.",
                    LocalDateTime.of(2024, 8, 17, 11, 20)
            );
            session.save(complaint6);

            session.getTransaction().commit();
            System.out.println("Sample complaints created successfully");

        } catch (HibernateException e) {
            System.err.println("Error creating sample complaints: " + e.getMessage());
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



    // הוספת הפונקציה להחזרת כל רכישות

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

    public static List<Complaints> getAllComplaintsOrderedByDate() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Complaints> query = builder.createQuery(Complaints.class);
            Root<Complaints> root = query.from(Complaints.class);

            // Order by complain date
            query.select(root).orderBy(builder.asc(root.get("complainDate")));

            List<Complaints> complaints = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return complaints;
        } catch (Exception e) {
            System.err.println("Error fetching complaints: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static List<PackageCard> getAllPackageCardsOrderedByDate() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PackageCard> query = builder.createQuery(PackageCard.class);
            Root<PackageCard> root = query.from(PackageCard.class);

            // Order by purchase date
            query.select(root).orderBy(builder.asc(root.get("purchaseDate")));

            List<PackageCard> packageCards = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return packageCards;
        } catch (Exception e) {
            System.err.println("Error fetching package cards: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static List<PurchaseLink> getAllPurchaseLinksOrderedByDate() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PurchaseLink> query = builder.createQuery(PurchaseLink.class);
            Root<PurchaseLink> root = query.from(PurchaseLink.class);

            // Order by purchase time
            query.select(root).orderBy(builder.asc(root.get("purchaseTime")));

            List<PurchaseLink> purchaseLinks = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return purchaseLinks;
        } catch (Exception e) {
            System.err.println("Error fetching purchase links: " + e.getMessage());
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
    //handle return ticket method:
    public static String handleReturnTicket(String type, String orderIdString, String customerIdString) {
        // Convert orderId and customerId to int before searching
        int orderId;
        int customerId;
        try {
            orderId = Integer.parseInt(orderIdString);
            customerId = Integer.parseInt(customerIdString);
        } catch (NumberFormatException e) {
            return "Return Ticket failed order id or Customer id wrong";
        }

        // Open the session and transaction
        Session session = ConnectToDatabase.getSessionFactory().openSession();
        session.beginTransaction();

        Object purchase = null;
        float returnedValue = 0;
        int linkId = orderId;

        try {
            if (type.equals("purchaseCard")) {
                System.out.println("Searching for PurchaseCard with order_id: " + orderId);

                // Search for the PurchaseCard by order_id
                purchase = session.get(purchaseCard.class, orderId);  // orderId is the field from the database mapping
                if (purchase == null) {
                    System.out.println("PurchaseCard with order_id " + orderId + " not found.");
                    session.getTransaction().rollback();
                    return "Return Ticket failed order id or Customer id wrong";
                }

                purchaseCard card = (purchaseCard) purchase;
                System.out.println("Found PurchaseCard: " + card);

                // Check if customerId matches
                if (card.getCustomerId() != customerId) {
                    System.out.println("Customer ID does not match.");
                    session.getTransaction().rollback();
                    return "Return Ticket failed order id or Customer id wrong";
                }

                // Check the time conditions against showtime
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime showTime = card.getShowTime();
                System.out.println(currentTime);
                System.out.println(showTime);

                if (currentTime.isBefore(showTime.minusHours(3))) {
                    returnedValue = card.getPrice();  // Full refund
                } else if (currentTime.isBefore(showTime.minusHours(1))) {
                    returnedValue = card.getPrice() / 2;  // Half refund
                } else {
                    returnedValue = 0;  // No refund
                }

                session.delete(card);  // Delete the PurchaseCard
                System.out.println("Deleted PurchaseCard: " + card);

            } else if (type.equals("PurchaseLink")) {
                System.out.println("Searching for PurchaseLink with order_id: " + linkId);

                // Search for the PurchaseLink by order_id (adjust the field if necessary)
                purchase = session.get(PurchaseLink.class, orderId);  // Adjust this to match the field name in PurchaseLink
                if (purchase == null) {
                    System.out.println("PurchaseLink with order_id " + orderId + " not found.");
                    session.getTransaction().rollback();
                    return "Return Ticket failed order id or Customer id wrong";
                }

                PurchaseLink link = (PurchaseLink) purchase;
                System.out.println("Found PurchaseLink: " + link);

                // Check if customerId matches
                if (link.getCustomerId() != customerId) {
                    System.out.println("Customer ID does not match.");
                    session.getTransaction().rollback();
                    return "Return Ticket failed order id or Customer id wrong";
                }

                // Check the time conditions against availableFrom
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime availableFrom = link.getAvailableFrom();

                if (currentTime.isBefore(availableFrom.minusHours(1))) {
                    returnedValue = link.getPrice() / 2;  // Half refund
                } else {
                    returnedValue = 0;  // No refund
                }

                session.delete(link);  // Delete the PurchaseLink
                System.out.println("Deleted PurchaseLink: " + link);
            }

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Transaction committed successfully.");
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            return "Return Ticket failed due to internal error.";
        } finally {
            session.close();
        }

        return "Return Ticket succeeded " + returnedValue;
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
    public static void addRequest(Request req) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(req);
            transaction.commit();
            System.out.println("Request saved successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.out.println("Failed to save the request");
        }
    }

    public static PackageCard savePackageCard(PackageCard packageCard) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(packageCard);  // Save the PackageCard object, and Hibernate will generate the ID
            transaction.commit();
            System.out.println("PackageCard saved successfully");
            return packageCard;  // Return the PackageCard object with the generated packageId
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            System.out.println("Failed to save the PackageCard");
            return null;  // Return null in case of failure
        }
    }
    public static String checkLinkByString(String link) {
        System.out.println("We are on connect to database and we are checking the link");
        System.out.println(link);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        // Use a query to find the PurchaseLink by the unique link value
        Query query = session.createQuery("FROM PurchaseLink WHERE uniqueLink = :link");
        query.setParameter("link", link);
        PurchaseLink purchaseLink = (PurchaseLink) query.uniqueResult();

        if (purchaseLink != null) {
            // Check if the link is available
            if (purchaseLink.isAvailable()) {
                String movieTitle = purchaseLink.getMovieTitle(); // Get the movie title
                session.getTransaction().commit();
                session.close();
                return "Link available: " + movieTitle;
            } else {
                session.getTransaction().commit();
                session.close();
                return "Link not available: out of time";
            }
        } else {
            session.getTransaction().commit();
            session.close();
            return "Link not available: link not found.";
        }
    }




}



