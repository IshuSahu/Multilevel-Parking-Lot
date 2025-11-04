package com.example.multilevel_parking_lot.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Level {
    private String id;
    private int levelNumber;
    private Map<String, ParkingSpot> spots = new ConcurrentHashMap<>();

    public int totalSpots() { return spots.size(); }
    public long availableSpots() {
        return spots.values().stream().filter(ParkingSpot::isAvailable).count();
    }
}
