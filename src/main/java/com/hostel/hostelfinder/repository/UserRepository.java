package com.hostel.hostelfinder.repository;

import com.hostel.hostelfinder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFullName(String fullName);
    boolean existsByEmail(String email);
    boolean existsByFullName(String fullName);
}