package com.example.multilevel_parking_lot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parking_spot")
@Getter @Setter @NoArgsConstructor
public class ParkingSpot {
    @Id
    private String id; // e.g. "L1-S1"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private Level level;

    private int number;

    @Column(name = "spot_type")
    private String spotType; // store enum name

    private boolean occupied;

    @Column(name = "current_ticket_id")
    private String currentTicketId;

    @Version
    private Long version; // optimistic locking
}