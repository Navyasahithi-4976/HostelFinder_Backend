package com.hostel.hostelfinder.dto;

import com.hostel.hostelfinder.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number format")
    private String phone;

    private User.UserType userType;

    // Fields for response only
    private Integer totalBookings;
    private Integer totalReviews;
    private Double averageRating;
}
