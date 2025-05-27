package com.hostel.hostelfinder.service.impl;

import com.hostel.hostelfinder.dto.BookingDTO;
import com.hostel.hostelfinder.entity.Booking;
import com.hostel.hostelfinder.entity.Hostel;
import com.hostel.hostelfinder.entity.User;
import com.hostel.hostelfinder.repository.BookingRepository;
import com.hostel.hostelfinder.repository.HostelRepository;
import com.hostel.hostelfinder.repository.UserRepository;
import com.hostel.hostelfinder.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HostelRepository hostelRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        User user = userRepository.findById(bookingDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Hostel hostel = hostelRepository.findById(bookingDTO.getHostelId())
            .orElseThrow(() -> new RuntimeException("Hostel not found"));

        validateBooking(bookingDTO, hostel);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setHostel(hostel);
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setNumberOfRooms(bookingDTO.getNumberOfRooms());
        booking.setTotalPrice(calculateTotalPrice(bookingDTO, hostel));
        booking.setStatus(Booking.BookingStatus.PENDING);

        // Update available rooms
        hostel.setAvailableRooms(hostel.getAvailableRooms() - booking.getNumberOfRooms());
        hostelRepository.save(hostel);

        return convertToDTO(bookingRepository.save(booking));
    }

    @Override
    public BookingDTO getBooking(Long id) {
        return convertToDTO(bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found")));
    }

    @Override
    public List<BookingDTO> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getHostelBookings(Long hostelId) {
        Hostel hostel = hostelRepository.findById(hostelId)
            .orElseThrow(() -> new RuntimeException("Hostel not found"));
        return bookingRepository.findByHostelId(hostelId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING &&
            booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel booking in current status");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);

        // Return rooms to available pool
        Hostel hostel = booking.getHostel();
        hostel.setAvailableRooms(hostel.getAvailableRooms() + booking.getNumberOfRooms());
        hostelRepository.save(hostel);

        return convertToDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDTO confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Can only confirm pending bookings");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return convertToDTO(bookingRepository.save(booking));
    }

    private void validateBooking(BookingDTO bookingDTO, Hostel hostel) {
        if (bookingDTO.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }

        if (bookingDTO.getCheckOutDate().isBefore(bookingDTO.getCheckInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        if (bookingDTO.getNumberOfRooms() > hostel.getAvailableRooms()) {
            throw new RuntimeException("Not enough rooms available");
        }

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                hostel.getId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate()
        );

        int bookedRooms = overlappingBookings.stream()
                .mapToInt(Booking::getNumberOfRooms)
                .sum();

        if (bookedRooms + bookingDTO.getNumberOfRooms() > hostel.getTotalRooms()) {
            throw new RuntimeException("Not enough rooms available for the selected dates");
        }
    }

    private BigDecimal calculateTotalPrice(BookingDTO bookingDTO, Hostel hostel) {
        long nights = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return hostel.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(bookingDTO.getNumberOfRooms()));
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getFullName());
        dto.setHostelId(booking.getHostel().getId());
        dto.setHostelName(booking.getHostel().getName());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumberOfRooms(booking.getNumberOfRooms());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
