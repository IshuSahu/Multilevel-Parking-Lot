package com.example.multilevel_parking_lot.service.fee;

import com.example.multilevel_parking_lot.model.ParkingTicket;

import java.math.BigDecimal;
import java.time.Duration;

public interface FeeStrategy {
    BigDecimal calculate(ParkingTicket ticket, Duration duration);
}
