package com.tang.demo_db.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RestaurantDTO {
    private String name;
    private String address;
    private Double rating;

    @JsonProperty("user_ratings_total")
    private Integer userRatingsTotal;

    @JsonProperty("price_level")
    private Integer priceLevel;

    @JsonProperty("place_id")
    private String placeId;

    private LocationDTO location;
    private List<String> types;
    private String website;
    private String phone;
    private List<String> photos;

    @JsonProperty("predicted_score")
    private Double predictedScore;

    @JsonProperty("adjusted_score")
    private Double adjustedScore;

    private List<ReviewDTO> reviews;

    @JsonProperty("opening_hours")
    private Map<String, Object> openingHours;

    @JsonProperty("location_context")
    private LocationContextDTO locationContext;
}