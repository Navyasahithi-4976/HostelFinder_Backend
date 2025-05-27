package com.hostel.hostelfinder.repository;

import com.hostel.hostelfinder.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {
    List<Hostel> findByPincode(String pincode);
    
    List<Hostel> findByPricePerNightLessThanEqual(BigDecimal price);
    
    @Query("SELECT h FROM Hostel h WHERE h.pincode = :pincode AND h.pricePerNight <= :maxPrice AND h.availableRooms > 0")
    List<Hostel> searchHostels(String pincode, BigDecimal maxPrice);
}
