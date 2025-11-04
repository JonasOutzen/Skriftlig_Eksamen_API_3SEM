package app.daos.impl;

import app.daos.IDAO;
import app.dtos.SkillDTO;
import app.entities.Skill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkillDAO implements IDAO<SkillDTO, Integer> {

    private static SkillDAO instance;
    private static EntityManagerFactory emf;

    public static SkillDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new SkillDAO();
        }
        return instance;
    }

    @Override
    public SkillDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill entity = em.find(Skill.class, id);
            return entity != null ? new SkillDTO(entity) : null;
        }
    }

    @Override
    public List<SkillDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<SkillDTO> query = em.createQuery(
                    "SELECT new app.dtos.SkillDTO(s) FROM Skill s", SkillDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public SkillDTO create(SkillDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill entity = dto.toEntity();
            em.persist(entity);
            em.getTransaction().commit();
            return new SkillDTO(entity);
        }
    }

    @Override
    public SkillDTO update(Integer id, SkillDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill entity = em.find(Skill.class, id);
            if (entity == null) throw new IllegalArgumentException("Skill not found: " + id);

            entity.setSkillName(dto.getSkillName());
            entity.setSkillCategory(dto.getSkillCategory());
            entity.setSkillDescription(dto.getSkillDescription());

            Skill merged = em.merge(entity);
            em.getTransaction().commit();
            return new SkillDTO(merged);
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill entity = em.find(Skill.class, id);
            if (entity != null) em.remove(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Skill entity = em.find(Skill.class, id);
            return entity != null;
        }
    }
}
