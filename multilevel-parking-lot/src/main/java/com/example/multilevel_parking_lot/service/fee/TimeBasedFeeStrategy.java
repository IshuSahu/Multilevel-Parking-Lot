package com.example.multilevel_parking_lot.service.fee;

import com.example.multilevel_parking_lot.model.ParkingTicket;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Component
public class TimeBasedFeeStrategy implements FeeStrategy {
    // example rates - override from config in real app
    private static final BigDecimal RATE_PER_HOUR_CAR = BigDecimal.valueOf(30);
    private static final BigDecimal RATE_PER_HOUR_MOTORCYCLE = BigDecimal.valueOf(10);
    private static final BigDecimal RATE_PER_HOUR_TRUCK = BigDecimal.valueOf(50);
    private static final BigDecimal RATE_PER_HOUR_EV = BigDecimal.valueOf(35);

    @Override
    public BigDecimal calculate(ParkingTicket ticket, Duration duration) {
        long hours = Math.max(1, (int) Math.ceil((double)duration.toMinutes() / 60.0));
        switch (ticket.getVehicleType()) {
            case "CAR": return RATE_PER_HOUR_CAR.multiply(BigDecimal.valueOf(hours));
            case "MOTORCYCLE": return RATE_PER_HOUR_MOTORCYCLE.multiply(BigDecimal.valueOf(hours));
            case "TRUCK": return RATE_PER_HOUR_TRUCK.multiply(BigDecimal.valueOf(hours));
            case "EV": return RATE_PER_HOUR_EV.multiply(BigDecimal.valueOf(hours));
            default: return RATE_PER_HOUR_CAR.multiply(BigDecimal.valueOf(hours));
        }
    }
}
