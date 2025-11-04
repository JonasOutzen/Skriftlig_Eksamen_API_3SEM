package app.dtos.servicedtos;

import app.dtos.CandidateDTO;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WrapperCandidateDetailsDTO {
    private CandidateDTO candidate;
    private List<SkillStatDTO> market;
}
