package com.example.multilevel_parking_lot.service.fee;

import com.example.multilevel_parking_lot.model.ParkingTicket;
import com.example.multilevel_parking_lot.model.RateCard;
import com.example.multilevel_parking_lot.service.RateCardService;
import com.example.multilevel_parking_lot.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TimeBasedFeeStrategy implements FeeStrategy {

    private final RateCardService rateCardService;

    @Override
    public BigDecimal calculate(ParkingTicket ticket, Duration duration) {
        String spotType = ticket.getVehicleType(); // or derive spot type via ticket->spot lookup (improve later)
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) tenantId = "default";

        BigDecimal ratePerHour = rateCardService
                .findRate(tenantId, spotType)
                .map(RateCard::getRatePerHour)
                .orElse(BigDecimal.valueOf(20.0)); // fallback

        long hours = Math.max(1, duration.toHours());
        return ratePerHour.multiply(BigDecimal.valueOf(hours));
    }
}
