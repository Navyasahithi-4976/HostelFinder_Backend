package com.hostel.hostelfinder.dto;

import lombok.Data;
import java.util.List;

@Data
public class SmartSearchResultDTO {
    private boolean exactMatch;
    private String searchedPincode;
    private List<HostelDTO> directResults;
    private List<HostelDTO> suggestedResults;
    private List<String> suggestedLocations;
    private String aiSuggestion;
}
