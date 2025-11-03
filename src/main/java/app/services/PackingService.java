package app.services;

import app.dtos.packing.PackingListResponse;
import app.entities.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.*;
import java.net.URI;

public class PackingService {
    //Insert new API here and rename method
    private static final String BASE_URL = "https://packingapi.cphbusinessapps.dk/packinglist/";
    private final ObjectMapper mapper = new ObjectMapper();

    public PackingListResponse getItemsForCategory(Category category) {
        String url = BASE_URL + category.name().toLowerCase();

        try {
            // 1. Build a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // 2. Send it
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Convert JSON → Java (using Jackson)
            return mapper.readValue(response.body(), PackingListResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new PackingListResponse(); // empty response fallback
        }
    }
}
