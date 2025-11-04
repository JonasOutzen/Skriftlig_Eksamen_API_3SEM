package app.restassured;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.config.Populate;
import app.security.utils.SecuritySeeder;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsEqual.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CandidateDAOTest {
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

    @Test
    @Order(1)
    void read() {
        given()
                .when().get("/candidates/1")
                .then()
                .statusCode(200)
                .body("candidateId", equalTo(1))
                .body("candidateName", equalTo("Alice Andersen"));
    }

    @Test
    @Order(2)
    void readAll() {
        given()
                .when().get("/candidates")
                .then()
                .statusCode(200)
                .body("candidate.size()", greaterThan(1));
    }

    @Test
    @Order(3)
    void create() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("""
                        {
                          "candidateName":"Test Candidate",
                          "candidatePhone":"+45 12 34 56 78",
                          "candidateEducation":"Software Engineering"
                        }
                        """)
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .body("candidateId", equalTo(4))
                .body("candidateName", equalTo("Test Candidate"))
                .body("candidatePhone", equalTo("+45 12 34 56 78"))
                .body("candidateEducation", equalTo("Software Engineering"));
    }

    @Test
    @Order(4)
    void update() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("""
                        {
                          "candidateName":"Test Candidate Updated",
                          "candidatePhone":"+45 12 34 56 78",
                          "candidateEducation":"Software Engineering"
                        }
                        """)
                .when()
                .put("/candidates/4")
                .then()
                .statusCode(200)
                .body("candidateId", equalTo(4))
                .body("candidateName", equalTo("Test Candidate Updated"))
                .body("candidatePhone", equalTo("+45 12 34 56 78"))
                .body("candidateEducation", equalTo("Software Engineering"));
    }

    @Test
    @Order(10)
    void delete() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .when()
                .delete("/candidates/4")
                .then()
                .statusCode(204);
        //204 means that there is no response body
    }

    @Test
    @Order(5)
    void validatePrimaryKey() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .when()
                .get("/candidates/1")
                .then()
                .statusCode(200)
                .body("candidateId", equalTo(1));
    }

    @Test
    @Order(6)
    void linkSkill() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .when()
                .put("/candidates/4/skills/1")
                .then()
                .statusCode(200)
                .body("candidateId", equalTo(4))
                .body("candidateName", equalTo("Test Candidate Updated"))
                .body("candidatePhone", equalTo("+45 12 34 56 78"))
                .body("candidateEducation", equalTo("Software Engineering"))
                .body("skillIds", hasItem(1))
                .body("skillCount", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(7)
    void findCandidatesBySkillCategory() {
        given()
                .when().get("/candidates?category=DB")
                .then().statusCode(200);
    }

    @Test
    @Order(8)
    void findSkillSlugsForCandidate() {
        given()
                .when()
                .get("/candidates/4")
                .then()
                .statusCode(200)
                .body("candidateId", equalTo(4))
                .body("candidateName", equalTo("Test Candidate Updated"))
                .body("candidatePhone", equalTo("+45 12 34 56 78"))
                .body("candidateEducation", equalTo("Software Engineering"))
                .body("skills.size()", greaterThan(0))
                .body("skills[0].skillSlug", notNullValue());

    }

    @Test
    @Order(9)
    void findAllIds() {
        given()
                .when()
                .get("/candidates")
                .then()
                .statusCode(200)
                .body("candidate.size()", greaterThan(0));
    }
}