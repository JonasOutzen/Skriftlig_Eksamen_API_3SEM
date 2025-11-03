package app.dtos.packing;

import java.util.List;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PackingListResponse {
    private List<PackingItemDTO> items;
}
