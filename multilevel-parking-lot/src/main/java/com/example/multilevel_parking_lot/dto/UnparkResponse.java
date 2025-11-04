package com.example.multilevel_parking_lot.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UnparkResponse {
    private boolean success;
    private String message;
    private String ticketId;
    private BigDecimal cost;
    private long durationMinutes;
}
