package com.hostel.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "hostels")
public class Hostel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String address;
    private String pincode;
    private BigDecimal pricePerNight;
    private Integer totalRooms;
    private Integer availableRooms;

    @ElementCollection
    @CollectionTable(name = "hostel_facilities")
    private List<String> facilities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "hostel_images")
    private List<String> images = new ArrayList<>();

    private Double rating;
    private Integer totalReviews;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (rating == null) rating = 0.0;
        if (totalReviews == null) totalReviews = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
