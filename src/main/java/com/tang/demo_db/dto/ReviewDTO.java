package com.tang.demo_db.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewDTO {
    @JsonProperty("author_name")
    private String authorName;

    private Double rating;
    private String text;
    private String time;

    @JsonProperty("relative_time_description")
    private String relativeTimeDescription;
}