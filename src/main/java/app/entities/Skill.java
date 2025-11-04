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
@Table(name = "skill",
        uniqueConstraints = @UniqueConstraint(name = "uk_skill_name", columnNames = "skill_name"))
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Integer skillId;

    @Column(name = "skill_name")
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_category")
    private SkillCategory skillCategory;

    @Column(name = "skill_description", length = 1000)
    private String skillDescription;

    @ManyToMany(mappedBy = "skills")
    @Builder.Default
    @ToString.Exclude
    private Set<Candidate> candidates = new HashSet<>();

    @Column(name = "skill_slug", nullable = false, unique = true, length = 100)
    private String skillSlug;

}
