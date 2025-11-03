package app.dtos;

import app.entities.Category;
import app.entities.Guide;
import app.entities.Trip;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripDTO {

    private Integer tripId;
    private String tripName;
    private LocalDate tripStartTime;
    private LocalDate tripEndTime;
    private String tripLocationCoordinates;
    private Float tripPrice;
    private Integer guideId;
    private String guideName;
    private Category tripCategory;
    private Float totalPackingWeight;

    public TripDTO(Trip trip) {
        this.tripId = trip.getTripId();
        this.tripName = trip.getTripName();
        this.tripStartTime = trip.getTripStartTime();
        this.tripEndTime = trip.getTripEndTime();
        this.tripLocationCoordinates = trip.getTripLocationCoordinates();
        this.tripPrice = trip.getTripPrice();
        this.tripCategory = trip.getTripCategory();
        if (trip.getGuide() != null) {
            this.guideId = trip.getGuide().getGuideId();
            this.guideName = trip.getGuide().getGuideName();
        }
    }

    public Trip toEntity() {
        Trip trip = new Trip();
        trip.setTripId(this.tripId != null ? this.tripId : 0);
        trip.setTripName(this.tripName);
        trip.setTripStartTime(this.tripStartTime);
        trip.setTripEndTime(this.tripEndTime);
        trip.setTripLocationCoordinates(this.tripLocationCoordinates);
        trip.setTripPrice(this.tripPrice);
        trip.setTripCategory(this.tripCategory);

        return trip;
    }

    public static List<TripDTO> toDTOList(List<Trip> trips) {
        return trips.stream().map(TripDTO::new).toList();
    }
}

