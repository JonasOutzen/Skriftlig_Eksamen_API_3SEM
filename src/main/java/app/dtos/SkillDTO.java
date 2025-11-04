package app.dtos;

import app.entities.Candidate;
import app.entities.Skill;
import app.entities.SkillCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillDTO {

    private Integer skillId;
    private String skillName;
    private SkillCategory skillCategory;
    private String skillDescription;
    private String skillSlug;

    // Many-to-many summary
    private List<Integer> candidateIds;
    private Integer candidateCount;

    public SkillDTO(Skill s) {
        this.skillId = s.getSkillId();
        this.skillName = s.getSkillName();
        this.skillCategory = s.getSkillCategory();
        this.skillDescription = s.getSkillDescription();
        this.skillSlug = s.getSkillSlug();

        Set<Candidate> candidates = s.getCandidates();
        if (candidates != null) {
            this.candidateIds = candidates.stream()
                    .map(Candidate::getCandidateId)
                    .collect(Collectors.toList());
            this.candidateCount = candidates.size();
        } else {
            this.candidateIds = List.of();
            this.candidateCount = 0;
        }
    }

    public Skill toEntity() {
        Skill s = new Skill();
        if (this.skillId != null) s.setSkillId(this.skillId);
        s.setSkillName(this.skillName);
        s.setSkillCategory(this.skillCategory);
        s.setSkillDescription(this.skillDescription);
        s.setSkillSlug(this.skillSlug);
        return s;
    }

    public static List<SkillDTO> toDTOList(List<Skill> skills) {
        return skills.stream().map(SkillDTO::new).toList();
    }
}
