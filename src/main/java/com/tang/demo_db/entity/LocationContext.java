package com.tang.demo_db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_context")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationContext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "search_area")
    private String searchArea;

    @Column(name = "location_type")
    private String locationType;

    private String country;
}