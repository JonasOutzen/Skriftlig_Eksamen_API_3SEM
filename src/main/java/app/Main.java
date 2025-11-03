package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.Populate;
import app.security.utils.SecuritySeeder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Populating database...");
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        Populate.seed(emf);
        SecuritySeeder.seedDefaults(emf);
        System.out.println("Done seeding!");

        ApplicationConfig.startServer(7070);}
}