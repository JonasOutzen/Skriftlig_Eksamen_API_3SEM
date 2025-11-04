package app.daos.impl;

import app.daos.IDAO;
import app.dtos.CandidateDTO;
import app.entities.Candidate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateDAO implements IDAO<CandidateDTO, Integer> {

    private static CandidateDAO instance;
    private static EntityManagerFactory emf;

    public static CandidateDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new CandidateDAO();
        }
        return instance;
    }

    @Override
    public CandidateDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate entity = em.find(Candidate.class, id);
            return entity != null ? new CandidateDTO(entity) : null;
        }
    }

    @Override
    public List<CandidateDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<CandidateDTO> query = em.createQuery(
                    "SELECT new app.dtos.CandidateDTO(c) FROM Candidate c", CandidateDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public CandidateDTO create(CandidateDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate entity = dto.toEntity();
            em.persist(entity);
            em.getTransaction().commit();
            return new CandidateDTO(entity);
        }
    }

    @Override
    public CandidateDTO update(Integer id, CandidateDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate entity = em.find(Candidate.class, id);
            if (entity == null) throw new IllegalArgumentException("Candidate not found: " + id);

            entity.setCandidateName(dto.getCandidateName());
            entity.setCandidatePhone(dto.getCandidatePhone());
            entity.setCandidateEducation(dto.getCandidateEducation());

            Candidate merged = em.merge(entity);
            em.getTransaction().commit();
            return new CandidateDTO(merged);
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate entity = em.find(Candidate.class, id);
            if (entity != null) em.remove(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate entity = em.find(Candidate.class, id);
            return entity != null;
        }
    }
}
