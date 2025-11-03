package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Setter

public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private int tripId;
    @Column(name = "trip_name")
    private String tripName;
    @Column(name = "trip_start_time")
    private LocalDate tripStartTime;
    @Column(name = "trip_end_time")
    private LocalDate tripEndTime;
    @Column(name = "trip_location_coordinates")
    private String tripLocationCoordinates;
    @Column(name = "trip_price")
    private float tripPrice;
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_category")
    private Category tripCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "guide_id", nullable = true,
            foreignKey = @ForeignKey(name = "fk_trip_guide"))
    @ToString.Exclude
    private Guide guide;


}