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

    public void readByCategory(Context ctx) {
        String categoryParam = ctx.pathParam("category");

        try {
            SkillCategory category = SkillCategory.valueOf(categoryParam.toUpperCase());
            List<CandidateDTO> filteredCandidateList = candidateDAO.findCandidatesBySkillCategory(category);

            if (filteredCandidateList.isEmpty()) {
                ctx.status(404).result("No candidates found with skill category: " + category);
                return;
            }

            ctx.status(200).json(filteredCandidateList);

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid category: " + categoryParam);
        }
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

        // Wrap and return (use imported WrapperCandidateDetailsDTO)
        var wrapper = WrapperCandidateDetailsDTO.builder()
                .candidate(candidate)
                .market(market)
                .build();

        ctx.status(200).json(wrapper, WrapperCandidateDetailsDTO.class);
    }


}
