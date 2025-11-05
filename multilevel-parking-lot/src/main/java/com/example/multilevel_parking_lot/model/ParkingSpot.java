package com.example.multilevel_parking_lot.model;

import com.example.multilevel_parking_lot.model.enums.SpotType;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;
@Data
@Builder
public class ParkingSpot {
    private String id; // unique id like L1-S01
    private SpotType spotType;
    private int level;
    private int number;

    // track occupancy in thread-safe manner, ensure non-null when using builder
    @Builder.Default
    private AtomicBoolean occupied = new AtomicBoolean(false);

    private String currentTicketId;

    public boolean occupy(String ticketId) {
        if (occupied.compareAndSet(false, true)) {
            this.currentTicketId = ticketId;
            return true;
        }
        return false;
    }

    public boolean free() {
        if (occupied.compareAndSet(true, false)) {
            this.currentTicketId = null;
            return true;
        }
        return false;
    }

    public boolean isAvailable() {
        return !occupied.get();
    }
}