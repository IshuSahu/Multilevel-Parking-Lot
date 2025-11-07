package com.example.multilevel_parking_lot.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RateCardResponse {
    private Long id;
    private String tenantId;
    private String spotType;
    private BigDecimal ratePerHour;
}

