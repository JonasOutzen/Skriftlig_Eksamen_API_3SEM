package app.dtos;

import app.dtos.packing.PackingItemDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrapperTripDetailsDTO {
    private TripDTO trip;
    private GuideDTO guide;
    private List<PackingItemDTO> packing;
}
