package app.config;

import app.entities.Category;
import app.entities.Guide;
import app.entities.Trip;

// --- security/domain (adjust packages if yours differ) ---
import app.security.entities.Role;
import app.security.entities.User;

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

            Guide bob = Guide.builder()
                    .guideName("Bob Jensen")
                    .guideEmail("bob@example.com")
                    .guidePhone("+45 55 66 77 88")
                    .guideYearsOfExperience(12.0f)
                    .build();
            em.persist(bob);

            Guide carla = Guide.builder()
                    .guideName("Carla Madsen")
                    .guideEmail("carla@example.com")
                    .guidePhone("+45 22 33 44 55")
                    .guideYearsOfExperience(2.0f)
                    .build();
            em.persist(carla);

            // ---------- TRIPS ----------
            createTrip(em,
                    "Copenhagen Canals Weekend",
                    LocalDate.of(2025, 5, 2),
                    LocalDate.of(2025, 5, 4),
                    "55.6761,12.5683",
                    1499.0f,
                    Category.CITY,
                    alice);

            createTrip(em,
                    "Skagen Dunes Retreat",
                    LocalDate.of(2025, 6, 13),
                    LocalDate.of(2025, 6, 15),
                    "57.7200,10.5839",
                    1899.0f,
                    Category.BEACH,
                    bob);

            createTrip(em,
                    "Rold Forest Hike",
                    LocalDate.of(2025, 9, 20),
                    LocalDate.of(2025, 9, 21),
                    "56.8167,9.8500",
                    899.0f,
                    Category.FOREST,
                    alice);

            createTrip(em,
                    "Bornholm Cliffs & Lakes",
                    LocalDate.of(2025, 7, 5),
                    LocalDate.of(2025, 7, 8),
                    "55.1604,14.8669",
                    2299.0f,
                    Category.SEA,
                    carla);

            createTrip(em,
                    "Aarhus City & Sea Day Trip",
                    LocalDate.of(2025, 8, 10),
                    LocalDate.of(2025, 8, 10),
                    "56.1629,10.2039",
                    499.0f,
                    Category.CITY,
                    bob);

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
