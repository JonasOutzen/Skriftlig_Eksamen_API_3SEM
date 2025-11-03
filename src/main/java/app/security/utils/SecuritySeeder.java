package app.security.utils;

import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.mindrot.jbcrypt.BCrypt;

public final class SecuritySeeder {
    private SecuritySeeder() {}

    // Convenient entrypoint (what you use in Main)
    public static void seedDefaults(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            seedDefaults(em);
            em.getTransaction().commit();
        }
    }

    // Also available if you ever want to seed within an existing transaction
    public static void seedDefaults(EntityManager em) {
        Role admin = getOrCreateRole(em, "ADMIN");
        Role user  = getOrCreateRole(em, "USER");

        getOrCreateUser(em, "jonas", "jonaspw", admin);
        getOrCreateUser(em, "user",  "test123", user);
    }


    private static Role getOrCreateRole(EntityManager em, String roleName) {
        var q = em.createQuery("SELECT r FROM Role r WHERE r.name = :n", Role.class);
        q.setParameter("n", roleName);
        var found = q.getResultList();
        if (!found.isEmpty()) return found.get(0);

        var r = new app.security.entities.Role();
        r.setRoleName(roleName);
        em.persist(r);
        return r;
    }


    private static User getOrCreateUser(EntityManager em, String username, String rawPassword, Role role) {
        var found = em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class)
                .setParameter("u", username)
                .getResultList();
        if (!found.isEmpty()) return found.get(0);

        User u = new User();
        u.setUsername(username);
        u.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        u.addRole(role);
        em.persist(u);
        return u;
    }
}
