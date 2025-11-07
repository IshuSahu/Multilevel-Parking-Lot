package com.example.multilevel_parking_lot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "parking_ticket")
@Getter @Setter @NoArgsConstructor
public class ParkingTicket {
    @Id
    private String ticketId;

    @Column(name = "parking_lot_id")
    private String parkingLotId;

    @Column(name = "spot_id")
    private String spotId;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "entry_time")
    private Instant entryTime;

    @Column(name = "exit_time")
    private Instant exitTime;

    private BigDecimal cost;

    private String status;
}