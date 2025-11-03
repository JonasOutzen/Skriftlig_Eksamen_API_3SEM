package app.dtos.packing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackingItemDTO {
    private String name;
    private int weightInGrams;
    private int quantity;
    private String description;
    private String category;
    private List<BuyingOptionDTO> buyingOptions;
}

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
class BuyingOptionDTO {
    private String shopName;
    private String shopUrl;
    private int price;
}
