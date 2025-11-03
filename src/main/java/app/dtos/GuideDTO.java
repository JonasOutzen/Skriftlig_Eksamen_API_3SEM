package app.dtos;

import app.entities.Guide;
import app.entities.Trip;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuideDTO {

    private Integer guideId;
    private String guideName;
    private String guideEmail;
    private String guidePhone;
    private float guideYearsOfExperience;

    private List<Integer> tripIds;
    private Integer tripCount;

    public GuideDTO(Guide guide) {
        this.guideId = guide.getGuideId();
        this.guideName = guide.getGuideName();
        this.guideEmail = guide.getGuideEmail();
        this.guidePhone = guide.getGuidePhone();
        this.guideYearsOfExperience = guide.getGuideYearsOfExperience();

        Set<Trip> trips = guide.getTripSet();
        if (trips != null) {
            this.tripIds = trips.stream().map(Trip::getTripId).collect(Collectors.toList());
            this.tripCount = trips.size();
        } else {
            this.tripIds = List.of();
            this.tripCount = 0;
        }
    }

    public Guide toEntity() {
        Guide guide = new Guide();

        if (this.guideId != null) {
            guide.setGuideId(this.guideId);
        }
        guide.setGuideName(this.guideName);
        guide.setGuideEmail(this.guideEmail);
        guide.setGuidePhone(this.guidePhone);
        guide.setGuideYearsOfExperience(this.guideYearsOfExperience);


        return guide;
    }

    public static List<GuideDTO> toDTOList(List<Guide> guides) {
        return guides.stream().map(GuideDTO::new).toList();
    }
}
