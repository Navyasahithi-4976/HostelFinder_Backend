package com.hostel.hostelfinder.service.impl;

import com.hostel.hostelfinder.dto.HostelDTO;
import com.hostel.hostelfinder.entity.Hostel;
import com.hostel.hostelfinder.repository.HostelRepository;
import com.hostel.hostelfinder.service.HostelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostelServiceImpl implements HostelService {

    private final HostelRepository hostelRepository;

    @Override
    public List<HostelDTO> getAllHostels() {
        return hostelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HostelDTO getHostel(Long id) {
        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));
        return convertToDTO(hostel);
    }

    @Override
    public List<HostelDTO> searchHostels(String location, BigDecimal maxPrice, List<String> facilities) {
        List<Hostel> hostels;
        if (maxPrice == null && (facilities == null || facilities.isEmpty())) {
            hostels = hostelRepository.findByPincode(location);
        } else {
            hostels = hostelRepository.searchHostels(location, maxPrice);
            if (facilities != null && !facilities.isEmpty()) {
                hostels = hostels.stream()
                    .filter(hostel -> hostel.getFacilities().containsAll(facilities))
                    .collect(Collectors.toList());
            }
        }
        return hostels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HostelDTO createHostel(HostelDTO hostelDTO) {
        Hostel hostel = new Hostel();
        updateHostelFromDTO(hostel, hostelDTO);
        return convertToDTO(hostelRepository.save(hostel));
    }

    @Override
    @Transactional
    public HostelDTO updateHostel(Long id, HostelDTO hostelDTO) {
        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));
        updateHostelFromDTO(hostel, hostelDTO);
        return convertToDTO(hostelRepository.save(hostel));
    }

    @Override
    @Transactional
    public void deleteHostel(Long id) {
        hostelRepository.deleteById(id);
    }

    private HostelDTO convertToDTO(Hostel hostel) {
        HostelDTO dto = new HostelDTO();
        dto.setId(hostel.getId());
        dto.setName(hostel.getName());
        dto.setDescription(hostel.getDescription());
        dto.setAddress(hostel.getAddress());
        dto.setPincode(hostel.getPincode());
        dto.setPricePerNight(hostel.getPricePerNight());
        dto.setTotalRooms(hostel.getTotalRooms());
        dto.setAvailableRooms(hostel.getAvailableRooms());
        dto.setFacilities(hostel.getFacilities());
        dto.setImages(hostel.getImages());
        dto.setRating(hostel.getRating());
        dto.setTotalReviews(hostel.getTotalReviews());
        return dto;
    }

    private void updateHostelFromDTO(Hostel hostel, HostelDTO dto) {
        hostel.setName(dto.getName());
        hostel.setDescription(dto.getDescription());
        hostel.setAddress(dto.getAddress());
        hostel.setPincode(dto.getPincode());
        hostel.setPricePerNight(dto.getPricePerNight());
        hostel.setTotalRooms(dto.getTotalRooms());
        hostel.setAvailableRooms(dto.getAvailableRooms());
        hostel.setFacilities(dto.getFacilities());
        hostel.setImages(dto.getImages());
    }
}
