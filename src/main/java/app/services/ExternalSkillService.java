// app/services/ExternalSkillService.java
package app.services;

import app.dtos.servicedtos.SkillStatsResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;

public class ExternalSkillService {
    private static final String BASE_URL =
            "https://apiprovider.cphbusinessapps.dk/api/v1/skills/stats?slugs=";
    private final ObjectMapper mapper = new ObjectMapper();

    public SkillStatsResponse getStatsForSlugs(String commaSeparatedSlugs) {
        if (commaSeparatedSlugs == null || commaSeparatedSlugs.isBlank()) {
            return new SkillStatsResponse();
        }
        String url = BASE_URL + commaSeparatedSlugs;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), SkillStatsResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new SkillStatsResponse();
        }
    }
}
