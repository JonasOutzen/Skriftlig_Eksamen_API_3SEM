package app.controllers.notimpl;

import app.config.HibernateConfig;
import app.daos.impl.CandidateDAO;
import app.dtos.CandidateDTO;
import app.dtos.servicedtos.SkillStatDTO;
import app.dtos.servicedtos.SkillStatsResponse;
import app.services.ExternalSkillService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class ReportController {

    private final CandidateDAO candidateDAO;
    private final ExternalSkillService externalSkillService = new ExternalSkillService();

    public ReportController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.candidateDAO = CandidateDAO.getInstance(emf);
    }

    public void topByPopularity(Context ctx) {
        List<Integer> ids = candidateDAO.findAllIds();

        Integer bestCandidateId = null;
        double bestAvg = -1.0;

        for (Integer id : ids) {
            var slugs = candidateDAO.findSkillSlugsForCandidate(id);

            if (slugs == null || slugs.isEmpty()) continue;

            String joined = slugs.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(String::toLowerCase)
                    .distinct()
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            if (joined.isBlank()) continue;

            SkillStatsResponse stats = externalSkillService.getStatsForSlugs(joined);
            List<SkillStatDTO> data = (stats != null) ? stats.getData() : List.of();
            if (data == null || data.isEmpty()) continue;

            // average but only available scores
            var popularity = data.stream()
                    .map(SkillStatDTO::getPopularityScore)
                    .filter(p -> p != null)
                    .mapToInt(Integer::intValue)
                    .average();

            if (popularity.isEmpty()) continue;

            double avg = popularity.getAsDouble();
            if (avg > bestAvg) {
                bestAvg = avg;
                bestCandidateId = id;
            }
        }

        if (bestCandidateId == null) {
            ctx.status(404).json(Map.of(
                    "message", "No candidate has popularity data"
            ));
            return;
        }

        ctx.status(200).json(Map.of(
                "candidateId", bestCandidateId,
                "averagePopularityScore", Math.round(bestAvg * 100.0) / 100.0
        ));
    }
}
