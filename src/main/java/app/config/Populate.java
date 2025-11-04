package app.config;

import app.entities.Candidate;
import app.entities.Skill;
import app.entities.SkillCategory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public final class Populate {

    private Populate() {}

    public static void seed(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // ---------- SKILLS ----------
            Skill java = Skill.builder()
                    .skillName("Java")
                    .skillSlug("Java")
                    .skillCategory(SkillCategory.PROG_LANG)
                    .skillDescription("General-purpose JVM language")
                    .build();
            Skill postgres = Skill.builder()
                    .skillName("PostgreSQL")
                    .skillSlug("PostgreSQL")
                    .skillCategory(SkillCategory.DB)
                    .skillDescription("Relational database")
                    .build();
            Skill docker = Skill.builder()
                    .skillName("Docker")
                    .skillSlug("Docker")
                    .skillCategory(SkillCategory.DEVOPS)
                    .skillDescription("Containerization platform")
                    .build();

            em.persist(java);
            em.persist(postgres);
            em.persist(docker);

            // ---------- CANDIDATES ----------
            Candidate alice = Candidate.builder()
                    .candidateName("Alice Andersen")
                    .candidatePhone("+45 11 22 33 44")
                    .candidateEducation("BSc Computer Science")
                    .build();

            Candidate bob = Candidate.builder()
                    .candidateName("Bob Bæk")
                    .candidatePhone("+45 33 44 55 66")
                    .candidateEducation("MSc Software Engineering")
                    .build();

            Candidate noskillLarry = Candidate.builder()
                    .candidateName("Larry no skills")
                    .candidatePhone("+45 12 44 12 66")
                    .candidateEducation("MSc Software Engineering")
                    .build();

            em.persist(alice);
            em.persist(bob);
            em.persist(noskillLarry);

            // Candidate is the owning side - this is why we add them to candidate
            addSkills(em, alice, java, postgres);
            addSkills(em, bob, java, docker);

            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    private static void addSkills(EntityManager em, Candidate candidate, Skill... skills) {
        for (Skill s : skills) {
            candidate.getSkills().add(s);
            s.getCandidates().add(candidate);
        }
        em.merge(candidate);
    }
}
