package com.example.multilevel_parking_lot.service;

import com.example.multilevel_parking_lot.model.RateCard;

import java.util.Optional;

public interface RateCardService {
    Optional<RateCard> findRate(String tenantId, String spotType);
}
