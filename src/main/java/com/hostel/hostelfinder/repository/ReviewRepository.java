package com.hostel.hostelfinder.repository;

import com.hostel.hostelfinder.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHostelId(Long hostelId);
    Page<Review> findByHostelId(Long hostelId, Pageable pageable);
    List<Review> findByUserId(Long userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hostel.id = :hostelId")
    Double getAverageRatingForHostel(@Param("hostelId") Long hostelId);
}
