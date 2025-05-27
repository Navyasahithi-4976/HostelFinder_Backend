package com.hostel.hostelfinder.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HostelDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String pincode;
    private BigDecimal pricePerNight;
    private Integer totalRooms;
    private Integer availableRooms;
    private List<String> facilities;
    private List<String> images;
    private Double rating;
    private Integer totalReviews;
}
