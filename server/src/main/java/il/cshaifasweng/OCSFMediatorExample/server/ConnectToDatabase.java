package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.HibernateException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalTime;
import java.util.List;

public class ConnectToDatabase {
    private static Session session;



    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        // Add ALL of your entities here. You can also try adding a whole package.

        configuration.addAnnotatedClass(Movie.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
}
public static void CreateDatabase() throws HibernateException {
    System.out.println("Data Creation is starting");;
//        Movie movie1 = new Movie(
//            "Inception",
//            LocalTime.of(14, 30),
//            "Sci-Fi",
//            148,
//            8.8f,
//            "Christopher Nolan",
//            "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO."
//    );
//
//    Movie movie2 = new Movie(
//            "The Shawshank Redemption",
//            LocalTime.of(16, 0),
//            "Drama",
//            142,
//            9.3f,
//            "Frank Darabont",
//            "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
//    );
//
//    Movie movie3 = new Movie(
//            "The Godfather",
//            LocalTime.of(18, 0),
//            "Crime",
//            175,
//            9.2f,
//            "Francis Ford Coppola",
//            "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son."
//    );
//
//    Movie movie4 = new Movie(
//            "The Dark Knight",
//            LocalTime.of(20, 0),
//            "Action",
//            152,
//            9.0f,
//            "Christopher Nolan",
//            "When the menace known as the Joker emerges from his mysterious past, he wreaks havoc and chaos on the people of Gotham."
//    );
//
//    Movie movie5 = new Movie(
//            "Pulp Fiction",
//            LocalTime.of(22, 30),
//            "Crime",
//            154,
//            8.9f,
//            "Quentin Tarantino",
//            "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption."
//    );
//
//    Movie movie6 = new Movie(
//            "Schindler's List",
//            LocalTime.of(10, 30),
//            "Biography",
//            195,
//            8.9f,
//            "Steven Spielberg",
//            "In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis."
//    );
//
//    Movie movie7 = new Movie(
//            "Fight Club",
//            LocalTime.of(12, 15),
//            "Drama",
//            139,
//            8.8f,
//            "David Fincher",
//            "An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more."
//    );
//    session.save(movie1);
//    session.flush();
//    session.save(movie2);
//    session.flush();
//    session.save(movie3);
//    session.flush();
//    session.save(movie4);
//    session.flush();
//    session.save(movie5);
//    session.flush();
//    session.save(movie6);
//    session.flush();
//    session.save(movie7);
//    session.flush();
    System.out.println("ABED IS THE BEST :DDDDD ");
}
    public static void updateShowtime(String title, LocalTime newShowtime) throws Exception {
        System.out.println("Update function reached...");
        SessionFactory sessionFactory = getSessionFactory();
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            // Using CriteriaBuilder to fetch the movie with the specified title
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
            temp.setReleaseDate(newShowtime);
            session.update(temp);  // Use update instead of save
            session.getTransaction().commit();

            System.out.println("Updated showtime for movie: " + title);
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.out.println("Error updating showtime: " + e.getMessage());
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static Session initializeDatabase() throws IOException {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.clear();
            CreateDatabase();
            session.getTransaction().commit();
        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        }
        finally {
            if(session!=null)
            {
                session.close();
            }
        }
        return null;
    }
    static List<Movie> getAllMovies() throws Exception {
        Session session = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
            Root<Movie> root = query.from(Movie.class);
            query.select(root);

            List<Movie> data = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return data;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback transaction if an exception occurs
            }
            e.printStackTrace();
            throw e; // rethrow the exception after logging
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
