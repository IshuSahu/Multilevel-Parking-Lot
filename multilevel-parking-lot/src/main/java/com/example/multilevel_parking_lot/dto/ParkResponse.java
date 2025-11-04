package com.example.multilevel_parking_lot.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ParkResponse {
    private boolean success;
    private String message;
    private String ticketId;
    private String spotId;
    private Instant entryTime;
}
