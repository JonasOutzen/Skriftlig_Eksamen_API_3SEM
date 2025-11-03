package app.daos.impl;

import app.daos.IDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class GuideDAO implements IDAO<GuideDTO, Integer> {

    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideDAO();
        }
        return instance;
    }

    @Override
    public GuideDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            return guide == null ? null : new GuideDTO(guide);
        }
    }

    @Override
    public List<GuideDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GuideDTO> query = em.createQuery(
                    "SELECT new app.dtos.GuideDTO(g) FROM Guide g",
                    GuideDTO.class
            );
            return query.getResultList();
        }
    }

    @Override
    public GuideDTO create(GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = guideDTO.toEntity();
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        }
    }

    @Override
    public GuideDTO update(Integer id, GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, id);

            guide.setGuideName(guideDTO.getGuideName());
            guide.setGuideEmail(guideDTO.getGuideEmail());
            guide.setGuidePhone(guideDTO.getGuidePhone());
            guide.setGuideYearsOfExperience(guideDTO.getGuideYearsOfExperience());
            Guide mergedGuide = em.merge(guide);
            em.getTransaction().commit();
            return mergedGuide != null ? new GuideDTO(mergedGuide) : null;
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();

                Guide guide = em.find(Guide.class, id);
                if (guide != null) {
                    // Detach all trips referencing this guide
                    if (guide.getTripSet() != null && !guide.getTripSet().isEmpty()) {
                        guide.getTripSet().forEach(trip -> trip.setGuide(null));
                        guide.getTripSet().clear();
                    }

                    em.remove(guide);
                }

                em.getTransaction().commit();
            } catch (RuntimeException ex) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw ex;
            }
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Guide guide = em.find(Guide.class, id);
            return guide != null;
        }
    }
}