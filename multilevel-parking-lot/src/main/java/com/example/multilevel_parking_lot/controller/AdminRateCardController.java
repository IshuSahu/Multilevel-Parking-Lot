package com.example.multilevel_parking_lot.controller;

import com.example.multilevel_parking_lot.dto.RateCardRequest;
import com.example.multilevel_parking_lot.dto.RateCardResponse;
import com.example.multilevel_parking_lot.model.RateCard;
import com.example.multilevel_parking_lot.repository.RateCardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Minimal admin API to manage RateCards.
 * In production protect this with authentication + authorization.
 */
@RestController
@RequestMapping("/api/admin/rates")
@RequiredArgsConstructor
public class AdminRateCardController {

    private final RateCardRepository rateCardRepository;

    @PostMapping
    public ResponseEntity<RateCardResponse> create(@Valid @RequestBody RateCardRequest req) {
        RateCard rc = new RateCard();
        rc.setTenantId(req.getTenantId().equalsIgnoreCase("default") ? null : req.getTenantId());
        rc.setSpotType(req.getSpotType());
        rc.setRatePerHour(req.getRatePerHour());
        RateCard saved = rateCardRepository.save(rc);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RateCardResponse> update(@PathVariable Long id, @Valid @RequestBody RateCardRequest req) {
        RateCard rc = rateCardRepository.findById(id).orElseThrow();
        rc.setTenantId(req.getTenantId().equalsIgnoreCase("default") ? null : req.getTenantId());
        rc.setSpotType(req.getSpotType());
        rc.setRatePerHour(req.getRatePerHour());
        RateCard saved = rateCardRepository.save(rc);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<RateCardResponse>> list(@RequestParam(required = false) String tenantId,
                                                       @RequestParam(required = false) String spotType) {
        List<RateCard> items;
        if (tenantId != null && spotType != null) {
            if (tenantId.equalsIgnoreCase("default")) tenantId = null;
            String finalTenantId = tenantId;
            items = rateCardRepository.findAll().stream()
                    .filter(r -> (finalTenantId == null ? r.getTenantId() == null : finalTenantId.equals(r.getTenantId())))
                    .filter(r -> spotType == null || r.getSpotType().equalsIgnoreCase(spotType))
                    .collect(Collectors.toList());
        } else {
            items = rateCardRepository.findAll();
        }
        List<RateCardResponse> out = items.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RateCardResponse> get(@PathVariable Long id) {
        RateCard rc = rateCardRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(toDto(rc));
    }

    private RateCardResponse toDto(RateCard rc) {
        return RateCardResponse.builder()
                .id(rc.getId())
                .tenantId(rc.getTenantId() == null ? "default" : rc.getTenantId())
                .spotType(rc.getSpotType())
                .ratePerHour(rc.getRatePerHour())
                .build();
    }
}
