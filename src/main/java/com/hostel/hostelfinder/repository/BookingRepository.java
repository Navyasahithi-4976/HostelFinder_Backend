package com.hostel.hostelfinder.repository;

import com.hostel.hostelfinder.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByHostelId(Long hostelId);
    
    @Query("SELECT b FROM Booking b WHERE b.hostel.id = :hostelId AND b.status = 'CONFIRMED' AND ((b.checkInDate BETWEEN :checkIn AND :checkOut) OR (b.checkOutDate BETWEEN :checkIn AND :checkOut))")
    List<Booking> findOverlappingBookings(Long hostelId, LocalDate checkIn, LocalDate checkOut);
    
    List<Booking> findByUserIdAndStatus(Long userId, Booking.BookingStatus status);
}
