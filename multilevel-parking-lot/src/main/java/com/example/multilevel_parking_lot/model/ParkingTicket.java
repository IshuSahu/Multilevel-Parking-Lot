package com.example.multilevel_parking_lot.model;

import com.example.multilevel_parking_lot.model.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class ParkingTicket {
    private String ticketId;
    private String parkingLotId;
    private String spotId;
    private String vehiclePlate;
    private String vehicleType;
    private Instant entryTime;
    private Instant exitTime;
    private BigDecimal cost;
    private TicketStatus status;
}
