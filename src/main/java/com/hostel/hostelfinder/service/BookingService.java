package com.hostel.hostelfinder.service;

import com.hostel.hostelfinder.dto.BookingDTO;

import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingDTO bookingDTO);
    BookingDTO getBooking(Long id);
    List<BookingDTO> getUserBookings(Long userId);
    List<BookingDTO> getHostelBookings(Long hostelId);
    BookingDTO cancelBooking(Long id);
    BookingDTO confirmBooking(Long id);
}
