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

public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private int guideId;
    @Column(name = "guide_name")
    private String guideName;
    @Column(name = "guide_email")
    private String guideEmail;
    @Column(name = "guide_phone")
    private String guidePhone;
    @Column(name = "guide_years_of_experience")
    private float guideYearsOfExperience;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    @ToString.Exclude
    private Set<Trip> tripSet = new HashSet<>();

}

