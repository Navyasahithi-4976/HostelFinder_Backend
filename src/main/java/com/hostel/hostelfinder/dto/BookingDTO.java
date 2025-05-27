package com.hostel.hostelfinder.dto;

import com.hostel.hostelfinder.entity.Booking;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long hostelId;
    private String hostelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfRooms;
    private BigDecimal totalPrice;
    private Booking.BookingStatus status;
}
