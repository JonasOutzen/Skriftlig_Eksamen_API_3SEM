package app.routes;

import app.routes.impl.CandidateRoutes;
import app.routes.impl.ReportRoutes;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final CandidateRoutes candidateRoutes = new CandidateRoutes();
    private final ReportRoutes reportRoutes = new ReportRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/candidates", candidateRoutes.getRoutes());
            path("/reports", reportRoutes.getRoutes());
        };
    }
}
