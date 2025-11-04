package app.daos.impl;

import app.daos.IDAO;
import app.dtos.CandidateDTO;
import app.entities.Candidate;
import app.entities.SkillCategory;
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
            Candidate candidate = em.find(Candidate.class, id);
            return candidate != null ? new CandidateDTO(candidate) : null;
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
            Candidate candidate = dto.toEntity();
            em.persist(candidate);
            em.getTransaction().commit();
            return new CandidateDTO(candidate);
        }
    }

    @Override
    public CandidateDTO update(Integer id, CandidateDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, id);
            if (candidate == null) throw new IllegalArgumentException("Candidate not found: " + id);

            candidate.setCandidateName(dto.getCandidateName());
            candidate.setCandidatePhone(dto.getCandidatePhone());
            candidate.setCandidateEducation(dto.getCandidateEducation());

            Candidate merged = em.merge(candidate);
            em.getTransaction().commit();
            return new CandidateDTO(merged);
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Candidate candidate = em.find(Candidate.class, id);
            if (candidate != null) em.remove(candidate);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Candidate candidate = em.find(Candidate.class, id);
            return candidate != null;
        }
    }

    public CandidateDTO linkSkill(Integer candidateId, Integer skillId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            var candidate = em.find(app.entities.Candidate.class, candidateId);
            if (candidate == null) throw new IllegalArgumentException("Candidate not found: " + candidateId);

            var skill = em.find(app.entities.Skill.class, skillId);
            if (skill == null) throw new IllegalArgumentException("Skill not found: " + skillId);

            candidate.getSkills().add(skill);
            skill.getCandidates().add(candidate);

            var merged = em.merge(candidate);
            em.getTransaction().commit();
            return new CandidateDTO(merged);
        }
    }

    public List<CandidateDTO> findCandidatesBySkillCategory(SkillCategory category) {
        try (var em = emf.createEntityManager()) {
            var query = em.createQuery(" SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE s.skillCategory = :category", app.entities.Candidate.class);
            query.setParameter("category", category);

            List<Candidate> candidates = query.getResultList();
            return CandidateDTO.toDTOList(candidates);
        }
    }

    public List<String> findSkillSlugsForCandidate(Integer candidateId) {
        try (var em = emf.createEntityManager()) {
            var q = em.createQuery(" SELECT s.skillSlug FROM Candidate c JOIN c.skills s WHERE c.candidateId = :candidateId", String.class);
            q.setParameter("candidateId", candidateId);
            return q.getResultList();
        }
    }

    public List<Integer> findAllIds() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("SELECT c.candidateId FROM Candidate c", Integer.class)
                    .getResultList();
        }
    }

}
