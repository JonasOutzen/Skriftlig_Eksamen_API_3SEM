package app.security.daos;


import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ApiException;
import app.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityDAO implements ISecurityDAO {

    private static ISecurityDAO instance;
    private static EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public UserDTO getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password))
                throw new ValidationException("Wrong password");
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = getEntityManager()) {

            //Checks if the user already exists
            User existing = em.find(User.class, username);
            if (existing != null)
                throw new EntityExistsException("User with username: " + username + " already exists");

            em.getTransaction().begin();

            //Finds role and adds it to new user
            Role userRole = em.find(Role.class, "user");
            if (userRole == null) {
                userRole = new Role("user");
                em.persist(userRole);
            }

            //Create new user and hash the password
            User userEntity = new User(username, password);
            userEntity.addRole(userRole);
            em.persist(userEntity);

            em.getTransaction().commit();
            return userEntity;

        } catch (EntityExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(400, e.getMessage());
        }
    }

    @Override
    public User addRole(UserDTO userDTO, String newRole) {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, userDTO.getUsername());
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + userDTO.getUsername());
            em.getTransaction().begin();
            Role role = em.find(Role.class, newRole);
            if (role == null) {
                role = new Role(newRole);
                em.persist(role);
            }
            user.addRole(role);
            //em.merge(user);
            em.getTransaction().commit();
            return user;
        }
    }
}

