package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.GuideDAO;
import app.daos.impl.TripDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideController implements IController<GuideDTO, Integer> {

    private final GuideDAO guideDAO;
    private final TripDAO tripDAO;

    public GuideController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.guideDAO = GuideDAO.getInstance(emf);
        this.tripDAO = TripDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid key").get();
        GuideDTO dto = guideDAO.read(id);
        ctx.res().setStatus(200);
        ctx.json(dto, GuideDTO.class);
    }

    @Override
    public void readAll(Context ctx) {
        List<GuideDTO> list = guideDAO.readAll();
        ctx.res().setStatus(200);
        ctx.json(list, GuideDTO.class);
    }


    @Override
    public void create(Context ctx) {
        GuideDTO req = validateEntity(ctx);
        GuideDTO created = guideDAO.create(req);
        ctx.res().setStatus(201);
        ctx.json(created, GuideDTO.class);
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        GuideDTO updated = guideDAO.update(id, validateEntity(ctx));
        ctx.res().setStatus(200);
        ctx.json(updated, GuideDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        guideDAO.delete(id);
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return guideDAO.validatePrimaryKey(id);
    }

    @Override
    public GuideDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(GuideDTO.class)
                .check(a -> a.getGuideName() != null && !a.getGuideName().isEmpty(), "Guide name must be set")
                .get();
    }
}
