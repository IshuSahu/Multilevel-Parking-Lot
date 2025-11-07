package com.example.multilevel_parking_lot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_card")
@Getter @Setter @NoArgsConstructor
public class RateCard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    private String tenantId; // nullable if global

    @Column(name = "spot_type")
    private String spotType;

    @Column(name = "rate_per_hour")
    private BigDecimal ratePerHour;
}