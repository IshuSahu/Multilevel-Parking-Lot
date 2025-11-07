package com.example.multilevel_parking_lot.service.impl;

import com.example.multilevel_parking_lot.model.RateCard;
import com.example.multilevel_parking_lot.repository.RateCardRepository;
import com.example.multilevel_parking_lot.service.RateCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RateCardServiceImpl implements RateCardService {

    private final RateCardRepository rateCardRepository;

    /**
     * Returns the rate card for a given tenant and spot type.
     * Priority:
     *   1️⃣ tenant-specific rate (tenantId + spotType)
     *   2️⃣ global default rate (no tenantId, same spotType)
     *   3️⃣ empty if nothing found
     */
    @Override
    public Optional<RateCard> findRate(String tenantId, String spotType) {
        // Try tenant-specific first
        if (tenantId != null && !tenantId.isBlank()) {
            Optional<RateCard> tenantRate = rateCardRepository.findByTenantIdAndSpotType(tenantId, spotType);
            if (tenantRate.isPresent()) {
                return tenantRate;
            }
        }

        // Fallback to global
        return rateCardRepository.findBySpotType(spotType);
    }
}
