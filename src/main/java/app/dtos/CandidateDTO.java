package app.dtos;

import app.entities.Candidate;
import app.entities.Skill;
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
public class CandidateDTO {

    private Integer candidateId;
    private String candidateName;
    private String candidatePhone;
    private String candidateEducation;

    // Many-to-many summary
    private List<Integer> skillIds;
    private Integer skillCount;

    public CandidateDTO(Candidate c) {
        this.candidateId = c.getCandidateId();
        this.candidateName = c.getCandidateName();
        this.candidatePhone = c.getCandidatePhone();
        this.candidateEducation = c.getCandidateEducation();

        Set<Skill> skills = c.getSkills();
        if (skills != null) {
            this.skillIds = skills.stream()
                    .map(Skill::getSkillId)
                    .collect(Collectors.toList());
            this.skillCount = skills.size();
        } else {
            this.skillIds = List.of();
            this.skillCount = 0;
        }
    }

    public Candidate toEntity() {
        Candidate c = new Candidate();
        if (this.candidateId != null) c.setCandidateId(this.candidateId);
        c.setCandidateName(this.candidateName);
        c.setCandidatePhone(this.candidatePhone);
        c.setCandidateEducation(this.candidateEducation);
        return c;
    }

    public static List<CandidateDTO> toDTOList(List<Candidate> candidates) {
        return candidates.stream().map(CandidateDTO::new).toList();
    }
}
