package com.tang.demo_db.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationContextDTO {
    @JsonProperty("search_area")
    private String searchArea;

    @JsonProperty("location_type")
    private String locationType;

    private String country;
}