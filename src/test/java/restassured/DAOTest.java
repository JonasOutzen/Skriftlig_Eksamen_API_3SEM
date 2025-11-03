package restassured;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.Populate;
import app.security.utils.SecuritySeeder;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOTest {
    private static Javalin app;
    private static String adminToken;

    @BeforeAll
    static void setup() {
        HibernateConfig.setTest(true);

        //Starter testserver
        ApplicationConfig.startServer(7071);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7071;
        RestAssured.basePath = "/api";

        //Setups the database with data - before the tests
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        Populate.seed(emf);
        SecuritySeeder.seedDefaults(emf);

        //Makes sure you're always logged in as an admin during tests - adding security
        adminToken = given()
                .contentType("application/json")
                .body("{\"username\":\"jonas\",\"password\":\"jonaspw\"}")
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

    }
}