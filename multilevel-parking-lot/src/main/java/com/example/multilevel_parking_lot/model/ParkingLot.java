package com.example.multilevel_parking_lot.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ParkingLot {
    private String id;
    private String name;
    private List<Level> levels = new ArrayList<>();
    private Map<String, ParkingSpot> spotIndex = new ConcurrentHashMap<>();
    private Map<String, ParkingTicket> ticketIndex = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public void indexSpots() {
        spotIndex.clear();
        for (Level l : levels) {
            l.getSpots().values().forEach(s -> spotIndex.put(s.getId(), s));
        }
    }

    // convenience
    public List<Level> getLevels() {
        return levels;
    }
}
