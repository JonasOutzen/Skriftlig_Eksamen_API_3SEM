package app.routes.impl;

import app.controllers.notimpl.ReportController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ReportRoutes {
    private final ReportController reportController = new ReportController();

    public EndpointGroup getRoutes() {
        return () -> {
            // US-6
            get("/candidates/top-by-popularity", reportController::topByPopularity);
        };
    }
}
