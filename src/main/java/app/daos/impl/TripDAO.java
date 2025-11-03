package app.daos.impl;

import app.daos.IDAO;
import app.dtos.TripDTO;
import app.dtos.packing.PackingItemDTO;
import app.entities.Category;
import app.entities.Guide;
import app.entities.Trip;
import app.services.PackingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripDAO implements IDAO<TripDTO, Integer> {

    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new TripDAO();
        }
        return instance;
    }

    @Override
    public TripDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            return trip == null ? null : new TripDTO(trip);
        }
    }

    @Override
    public List<TripDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            var q = em.createQuery("SELECT t FROM Trip t", Trip.class);
            return q.getResultList().stream()
                    .map(TripDTO::new)
                    .toList();
        }
    }

    @Override
    public TripDTO create(TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Trip trip = tripDTO.toEntity();

            if (tripDTO.getGuideId() != null) {
                Guide guide = em.find(Guide.class, tripDTO.getGuideId());
                if (guide == null) {
                    throw new IllegalArgumentException("Guide with id " + tripDTO.getGuideId() + " not found");
                }
                trip.setGuide(guide);
            }

            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public TripDTO update(Integer id, TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Trip trip = em.find(Trip.class, id);
            if (trip == null) {
                em.getTransaction().commit();
                return null;
            }

            trip.setTripName(tripDTO.getTripName());
            trip.setTripStartTime(tripDTO.getTripStartTime());
            trip.setTripEndTime(tripDTO.getTripEndTime());
            trip.setTripPrice(tripDTO.getTripPrice());
            trip.setTripLocationCoordinates(tripDTO.getTripLocationCoordinates());
            trip.setTripCategory(tripDTO.getTripCategory());

            if (tripDTO.getGuideId() != null) {
                Guide guide = em.find(Guide.class, tripDTO.getGuideId());
                if (guide == null) {
                    throw new IllegalArgumentException("Guide with id " + tripDTO.getGuideId() + " not found");
                }
                trip.setGuide(guide);
            }

            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, id);
            if (trip != null) {
                em.remove(trip);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, id);
            return trip != null;
        }
    }

    public TripDTO assignGuideToTrip(Integer tripId, Integer guideId) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();

                Trip trip = em.find(Trip.class, tripId);
                if (trip == null) {
                    em.getTransaction().rollback();
                    return null; // controller returns 404 Trip not found
                }

                Guide newGuide = em.find(Guide.class, guideId);
                if (newGuide == null) {
                    em.getTransaction().rollback();
                    throw new IllegalArgumentException("Guide with id " + guideId + " not found");
                }

                // keep both sides in sync (handles reassignment too)
                Guide oldGuide = trip.getGuide();
                if (oldGuide != null && oldGuide != newGuide) {
                    oldGuide.getTripSet().remove(trip);
                }

                trip.setGuide(newGuide);
                newGuide.getTripSet().add(trip);

                em.getTransaction().commit();
                return new TripDTO(trip);
            } catch (RuntimeException ex) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw ex;
            }
        }
    }

    public Float fullWeightForTrip(Integer tripId) {
        try (EntityManager em = emf.createEntityManager()) {
            Trip trip = em.find(Trip.class, tripId);
            if (trip == null) return null;

            PackingService packingService = new PackingService();

            // Adjust this depending on your PackingService
            List<PackingItemDTO> packingItems = List.of();
            var response = packingService.getItemsForCategory(trip.getTripCategory());
            if (response != null && response.getItems() != null)
                packingItems = response.getItems();

            float totalWeight = 0f;
            for (PackingItemDTO item : packingItems) {
                totalWeight += item.getWeightInGrams() * item.getQuantity();
            }

            return totalWeight;
        }
    }

    public List<TripDTO> allTripsFromCategory(Category category) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Trip> query = em.createQuery("SELECT t FROM Trip t WHERE t.tripCategory = :category", Trip.class);
            query.setParameter("category", category);

            //Finds the trips from query
            List<Trip> trips = query.getResultList();
            //Maps to DTOS and puts them in the returned list
            List<TripDTO> tripsFromCategory = trips.stream()
                    .map(TripDTO::new)
                    .toList();

            return tripsFromCategory;
        }
    }

    public List<Map<String, Object>> totalPriceForGuides() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT t.guide.guideId, t.guide.guideName, SUM(t.tripPrice) " +
                       "FROM Trip t " +
                       "GROUP BY t.guide.guideId, t.guide.guideName",
                        Object[].class
            );

            List<Object[]> results = query.getResultList();

            // Convert to List<Map<String, Object>>
            List<Map<String, Object>> totals = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> guideTotal = new HashMap<>();
                guideTotal.put("guideId", row[0]);
                guideTotal.put("guideName", row[1]);
                guideTotal.put("totalPrice", row[2]);
                totals.add(guideTotal);
            }

            return totals;
        }
    }

}





