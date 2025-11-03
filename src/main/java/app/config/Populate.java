package app.config;

import app.entities.Category;
import app.entities.Guide;
import app.entities.Trip;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

public final class Populate {

    private Populate() {}

    public static void seed(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // ---------- GUIDES ----------
            Guide alice = Guide.builder()
                    .guideName("Alice Andersen")
                    .guideEmail("alice@example.com")
                    .guidePhone("+45 11 22 33 44")
                    .guideYearsOfExperience(5.5f)
                    .build();
            em.persist(alice);

            // ---------- TRIPS ----------
            createTrip(em,
                    "Copenhagen Canals Weekend",
                    LocalDate.of(2025, 5, 2),
                    LocalDate.of(2025, 5, 4),
                    "55.6761,12.5683",
                    1499.0f,
                    Category.CITY,
                    alice);


            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    private static Trip createTrip(EntityManager em,
                                   String name,
                                   LocalDate start,
                                   LocalDate end,
                                   String locationCoordinates,
                                   float price,
                                   Category category,
                                   Guide guide) {
        Trip trip = Trip.builder()
                .tripName(name)
                .tripStartTime(start)
                .tripEndTime(end)
                .tripLocationCoordinates(locationCoordinates)
                .tripPrice(price)
                .tripCategory(category)
                .guide(guide)
                .build();

        em.persist(trip);
        // keep inverse side in sync if Guide maintains tripSet
        guide.getTripSet().add(trip);
        return trip;
    }
}
