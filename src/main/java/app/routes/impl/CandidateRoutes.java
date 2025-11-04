package app.routes.impl;

import app.controllers.impl.CandidateController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CandidateRoutes {

    private final CandidateController candidateController = new CandidateController();

    public EndpointGroup getRoutes() {
        return () -> {
            // post
            post("/", candidateController::create, Role.ADMIN);

            // get
            get("/", candidateController::readAll);
            get("/{id}", candidateController::read);

            // put
            put("/{id}", candidateController::update, Role.ADMIN);

            // del
            delete("/{id}", candidateController::delete, Role.ADMIN);

            // Assigning skills to candidates
            put("/{candidateId}/skills/{skillId}", candidateController::attachSkill, Role.ADMIN);
        };
    }
}
