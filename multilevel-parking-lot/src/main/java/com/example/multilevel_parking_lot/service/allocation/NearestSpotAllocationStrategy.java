package com.example.multilevel_parking_lot.service.allocation;

import com.example.multilevel_parking_lot.model.Level;
import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.ParkingSpot;
import com.example.multilevel_parking_lot.model.Vehicle;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;

@Component
public class NearestSpotAllocationStrategy implements SpotAllocationStrategy {
    @Override
    public Optional<ParkingSpot> allocate(ParkingLot parkingLot, Vehicle vehicle) {
        // simple strategy: iterate levels in order and pick first available spot that fits
        return parkingLot.getLevels().stream()
                .sorted(Comparator.comparingInt(Level::getLevelNumber))
                .flatMap(level -> level.getSpots().values().stream())
                .filter(ParkingSpot::isAvailable)
                .filter(spot -> spot.getSpotType().canFitVehicle(vehicle.getVehicleType()))
                .sorted(Comparator.comparingInt(ParkingSpot::getNumber))
                .findFirst();
    }
}
