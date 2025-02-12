package com.tang.demo_db.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "preference")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preference implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String dietPreference;
    private String cuisinesPreference;

    @Enumerated(EnumType.STRING)
    private PricePreference pricePreference;

    public enum PricePreference {
        LOW, MEDIUM, HIGH
    }

    // Getters and Setters
}
