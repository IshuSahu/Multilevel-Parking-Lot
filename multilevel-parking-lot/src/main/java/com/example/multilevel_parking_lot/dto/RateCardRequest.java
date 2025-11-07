package com.example.multilevel_parking_lot.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RateCardRequest {
    // set tenantId to "default" for global
    @NotBlank
    private String tenantId;

    @NotBlank
    private String spotType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal ratePerHour;
}
