package com.example.multilevel_parking_lot.repository;

import com.example.multilevel_parking_lot.model.RateCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateCardRepository extends JpaRepository<RateCard, Long> {
    Optional<RateCard> findByTenantIdAndSpotType(String tenantId, String spotType);
    Optional<RateCard> findBySpotType(String spotType);
}
