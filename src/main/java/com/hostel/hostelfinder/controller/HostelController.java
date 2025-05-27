package com.hostel.hostelfinder.controller;

import com.hostel.hostelfinder.dto.HostelDTO;
import com.hostel.hostelfinder.service.HostelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/hostels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HostelController {

    private final HostelService hostelService;

    @GetMapping
    public ResponseEntity<List<HostelDTO>> getAllHostels() {
        return ResponseEntity.ok(hostelService.getAllHostels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HostelDTO> getHostel(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getHostel(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HostelDTO>> searchHostels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> facilities) {
        return ResponseEntity.ok(hostelService.searchHostels(location, maxPrice, facilities));
    }

    @PostMapping
    public ResponseEntity<HostelDTO> createHostel(@Valid @RequestBody HostelDTO hostelDTO) {
        return ResponseEntity.ok(hostelService.createHostel(hostelDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HostelDTO> updateHostel(
            @PathVariable Long id,
            @Valid @RequestBody HostelDTO hostelDTO) {
        return ResponseEntity.ok(hostelService.updateHostel(id, hostelDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHostel(@PathVariable Long id) {
        hostelService.deleteHostel(id);
        return ResponseEntity.ok().build();
    }
}
