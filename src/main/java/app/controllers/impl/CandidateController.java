package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.CandidateDAO;
import app.dtos.servicedtos.WrapperCandidateDetailsDTO;
import app.dtos.servicedtos.SkillStatsResponse;

import app.services.ExternalSkillService;
import app.dtos.CandidateDTO;
import app.daos.impl.SkillDAO;
import app.entities.SkillCategory;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class CandidateController implements IController<CandidateDTO, Integer> {

    private final CandidateDAO candidateDAO;
    private final SkillDAO skillDAO;
    private final ExternalSkillService externalSkillService = new ExternalSkillService();

    public CandidateController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.candidateDAO = CandidateDAO.getInstance(emf);
        this.skillDAO = SkillDAO.getInstance(emf);
    }


    @Override
    public void read(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        CandidateDTO dto = candidateDAO.read(id);
        if (dto == null) {
            ctx.status(404).result("Candidate not found");
            return;
        }
        ctx.status(200).json(dto);
    }

    @Override
    public void readAll(Context ctx) {
        // read-all with category /candidates?category={category}
        String categoryParam = ctx.queryParam("category");
        if (categoryParam != null && !categoryParam.isBlank()) {
            try {
                SkillCategory category = SkillCategory.valueOf(categoryParam.toUpperCase(java.util.Locale.ROOT));
                List<CandidateDTO> filtered = candidateDAO.findCandidatesBySkillCategory(category);

                ctx.status(200).json(filtered);

                return;
            } catch (IllegalArgumentException e) {
                ctx.status(404).result("Invalid category: " + categoryParam);
                return;
            }
        }

        // Normal readall without ?category just /candidates
        List<CandidateDTO> all = candidateDAO.readAll();
        ctx.status(200).json(all);
    }

    @Override
    public void create(Context ctx) {
        CandidateDTO req = validateEntity(ctx);
        CandidateDTO created = candidateDAO.create(req);
        ctx.status(201).json(created);
    }


    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id").get();

        CandidateDTO req = validateEntity(ctx);
        CandidateDTO updated = candidateDAO.update(id, req);
        if (updated == null) {
            ctx.status(404).result("Candidate not found");
            return;
        }
        ctx.status(200).json(updated);
    }


    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id").get();

        candidateDAO.delete(id);
        ctx.status(204);
    }


    public void attachSkill(Context ctx) {
        int candidateId = ctx.pathParamAsClass("candidateId", Integer.class).get();
        int skillId = ctx.pathParamAsClass("skillId", Integer.class).get();

        try {
            CandidateDTO updated = candidateDAO.linkSkill(candidateId, skillId);
            ctx.status(200).json(updated);
        } catch (IllegalArgumentException e) {
            ctx.status(404).result(e.getMessage());
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        return candidateDAO.validatePrimaryKey(id);
    }

    @Override
    public CandidateDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(CandidateDTO.class)
                .check(c -> c.getCandidateName() != null && !c.getCandidateName().isBlank(), "Name is required")
                .check(c -> c.getCandidatePhone() != null && !c.getCandidatePhone().isBlank(), "Phone is required")
                .check(c -> c.getCandidateEducation() != null && !c.getCandidateEducation().isBlank(), "Education is required")
                .get();
    }

    public void readDetails(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Finds candidate
        CandidateDTO candidate = candidateDAO.read(id);
        if (candidate == null) {
            ctx.status(404).result("Candidate not found");
            return;
        }

        // Collect slugs
        var slugs = candidateDAO.findSkillSlugsForCandidate(id);

        // Join into commaseparated string
        String joined = slugs.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::toLowerCase)
                .distinct()
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        // If no slugs - return empty list
        List<app.dtos.servicedtos.SkillStatDTO> market =
                joined.isBlank()
                        ? java.util.List.of()
                        : (externalSkillService.getStatsForSlugs(joined).getData() == null
                        ? java.util.List.of()
                        : externalSkillService.getStatsForSlugs(joined).getData());

        // Wrap and return
        var wrapper = WrapperCandidateDetailsDTO.builder()
                .candidate(candidate)
                .market(market)
                .build();

        ctx.status(200).json(wrapper, WrapperCandidateDetailsDTO.class);
    }


}
