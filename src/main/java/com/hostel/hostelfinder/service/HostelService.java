package com.hostel.hostelfinder.service;

import com.hostel.hostelfinder.dto.HostelDTO;

import java.math.BigDecimal;
import java.util.List;

public interface HostelService {
    List<HostelDTO> getAllHostels();
    HostelDTO getHostel(Long id);
    List<HostelDTO> searchHostels(String location, BigDecimal maxPrice, List<String> facilities);
    HostelDTO createHostel(HostelDTO hostelDTO);
    HostelDTO updateHostel(Long id, HostelDTO hostelDTO);
    void deleteHostel(Long id);
}
