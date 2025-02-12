package com.tang.demo_db.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private Double rating;

    @Column(name = "user_ratings_total")
    private Integer userRatingsTotal;

    @Column(name = "price_level")
    private Integer priceLevel;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String types;

    private String website;

    @Column(name = "phone")
    private String phone;

    @Column(columnDefinition = "JSON")
    private String photos;

    @Column(name = "predicted_score")
    private Double predictedScore;

    @Column(name = "adjusted_score")
    private Double adjustedScore;

    @Column(name = "opening_hours", columnDefinition = "JSON")
    private String openingHours;

    // Relationships
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private LocationContext locationContext;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Favourite> favourites = new ArrayList<>();

    // Helper method to set location
    public void setLocation(Double lat, Double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }

    // Helper method to convert types list to string
    public void setTypesList(List<String> typesList) {
        this.types = String.join(",", typesList);
    }

    // Helper method to get types as list
    public List<String> getTypesList() {
        if (types == null || types.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(types.split(","));
    }
}