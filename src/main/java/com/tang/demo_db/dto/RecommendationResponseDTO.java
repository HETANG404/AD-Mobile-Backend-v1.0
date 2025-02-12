package com.tang.demo_db.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationResponseDTO {
    private boolean success;
    private String message;
    private List<RestaurantDTO> restaurants;
}
