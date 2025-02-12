package com.tang.demo_db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private com.tang.demo_db.entity.Restaurant restaurant;

    @Column(name = "author_name")
    private String authorName;

    private Double rating;

    @Column(columnDefinition = "TEXT")
    private String text;

    private String time;

    @Column(name = "relative_time_description")
    private String relativeTimeDescription;
}