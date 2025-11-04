package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Setter
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Integer candidateId;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "candidate_phone")
    private String candidatePhone;

    @Column(name = "candidate_education")
    private String candidateEducation;

    @ManyToMany
    @JoinTable(
            name = "candidate_skill",
            joinColumns = @JoinColumn(name = "candidate_id", foreignKey = @ForeignKey(name = "fk_candidate_skill_candidate")),
            inverseJoinColumns = @JoinColumn(name = "skill_id", foreignKey = @ForeignKey(name = "fk_candidate_skill_skill"))
    )
    @Builder.Default
    @ToString.Exclude
    private Set<Skill> skills = new HashSet<>();
}
