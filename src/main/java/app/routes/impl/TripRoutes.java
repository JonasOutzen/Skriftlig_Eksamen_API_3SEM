package app.routes.impl;

import app.controllers.impl.TripController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private final TripController tripController = new TripController();

    public EndpointGroup getRoutes() {
        return () -> {
            // Post
            post("/", tripController::create, Role.ADMIN);

            // Get
            get("/guides/totalprice", tripController::getTotalPriceForAllGuides);
            //ReadAll works both with category search and to read all
            get("/", tripController::readAll);
            get("/{id}", tripController::read);
            get("/{id}/packing/weight", tripController::getFullWeight);

            // Put
            put("/{id}", tripController::update, Role.ADMIN);
            put("/{tripid}/guides/{guideid}", tripController::assignGuideToTrip, Role.ADMIN);

            // Delete
            delete("/{id}", tripController::delete, Role.ADMIN);

        };
    }
}
