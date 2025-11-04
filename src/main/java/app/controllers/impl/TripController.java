package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.GuideDAO;
import app.daos.impl.TripDAO;
import app.dtos.CandidateDTO;
import app.dtos.SkillDTO;
import app.dtos.WrapperTripDetailsDTO;
import app.entities.SkillCategory;
import app.services.PackingService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class TripController implements IController<SkillDTO, Integer> {

    private final TripDAO tripDAO;
    private final GuideDAO guideDAO;

    public TripController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.tripDAO = TripDAO.getInstance(emf);
        this.guideDAO = GuideDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // What we are requesting
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid key").get();
        // The DTO
        SkillDTO trip = tripDAO.read(id);
        if (trip == null) {
            ctx.status(404).result("Trip not found");
            return;
        }

        // Finds the guide info for the trip, so we can show it later
        CandidateDTO guide = (trip.getGuideId() != null) ? guideDAO.read(trip.getGuideId()) : null;

        // Packing items From external API
        var packingService = new PackingService();
        var packingResponse = packingService.getItemsForCategory(trip.getTripSkillCategory());
        var packingItems = packingResponse.getItems();

        // Put everything together
        WrapperTripDetailsDTO fullTrip = WrapperTripDetailsDTO.builder()
                .trip(trip)
                .guide(guide)
                .packing(packingItems)
                .build();

        // Response
        ctx.res().setStatus(200);
        ctx.json(fullTrip, WrapperTripDetailsDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        String categoryParameter = ctx.queryParam("category");

        if (categoryParameter != null && !categoryParameter.isBlank()) {
            try {
                SkillCategory skillCategory = SkillCategory.valueOf(categoryParameter.toUpperCase());
                List<SkillDTO> trips = tripDAO.allTripsFromCategory(skillCategory);

                if (trips.isEmpty()) {
                    ctx.status(404).result("No trips found for category: " + skillCategory);
                } else {
                    ctx.status(200).json(trips);
                }

            } catch (IllegalArgumentException e) {
                ctx.status(400).result("Invalid category: " + categoryParameter);
            }
        } else {
            List<SkillDTO> allTrips = tripDAO.readAll();
            ctx.status(200).json(allTrips);
        }
    }

    @Override
    public void create(Context ctx) {
        // Request
        SkillDTO jsonRequest = ctx.bodyAsClass(SkillDTO.class);
        // DTO
        SkillDTO skillDTO = tripDAO.create(jsonRequest);
        // Response
        ctx.res().setStatus(201);
        ctx.json(skillDTO, SkillDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        // DTO
        SkillDTO skillDTO = tripDAO.update(id, validateEntity(ctx));
        // Response
        ctx.res().setStatus(200);
        ctx.json(skillDTO, SkillDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();
        tripDAO.delete(id);
        // Response
        ctx.status(200).result("Trip with id " + id + " deleted successfully");
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return tripDAO.validatePrimaryKey(integer);
    }

    @Override
    public SkillDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(SkillDTO.class)
                .check(s -> s.getTripName() != null && !s.getTripName().isEmpty(), "Trip name must be set")
                .check(s -> s.getGuideId() != null, "Guide ID must be set")
                .check(s -> s.getTripLocationCoordinates() != null && !s.getTripLocationCoordinates().isEmpty(), "Location must be set")
                .check(s -> s.getTripStartTime() != null, "Trip start time must be set")
                .check(s -> s.getTripEndTime() != null, "Trip end time must be set")
                .check(s -> s.getTripSkillCategory() != null, "Trip category must be set")
                .check(s -> s.getTripPrice() != null, "Trip price must be set")
                .get();
    }

    // app.controllers.impl.TripController
    public void assignGuideToTrip(Context ctx) {
        int tripId  = ctx.pathParamAsClass("tripid", Integer.class).get();
        int guideId = ctx.pathParamAsClass("guideid", Integer.class).get();

        try {
            SkillDTO updated = tripDAO.assignGuideToTrip(tripId, guideId);
            if (updated == null) {
                ctx.status(404).result("Trip not found");
                return;
            }
            ctx.status(200).json(updated, SkillDTO.class);
        } catch (IllegalArgumentException e) {
            // thrown when the guide doesn't exist (see DAO below)
            ctx.status(404).result(e.getMessage());
        }
    }

    public void getFullWeight(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        Float total = tripDAO.fullWeightForTrip(id);
        if (total == null) {
            ctx.status(404).result("Trip not found");
            return;
        }
        ctx.json(Map.of(
                "tripId", id,
                "totalWeightGrams", total
        ));
    }

    public void getTotalPriceForAllGuides(Context ctx) {
        List<Map<String, Object>> totals = tripDAO.totalPriceForGuides();

        if (totals.isEmpty()) {
            ctx.status(404).result("No guides found with trips");
            return;
        }

        ctx.status(200).json(totals);
    }
}
