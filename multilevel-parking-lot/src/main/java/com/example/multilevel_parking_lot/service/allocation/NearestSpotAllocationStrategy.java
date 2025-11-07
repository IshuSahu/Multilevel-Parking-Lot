package com.example.multilevel_parking_lot.service.allocation;

import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.ParkingSpot;
import com.example.multilevel_parking_lot.model.enums.VehicleType;
import com.example.multilevel_parking_lot.service.impl.SpotTypeMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Component
public class NearestSpotAllocationStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<ParkingSpot> allocate(ParkingLot lot, VehicleType vehicleType) {
        String[] allowedTypes = SpotTypeMapper.typesForVehicle(vehicleType);

        // Flatten all levels -> spots and filter available + compatible
        return lot.getLevels().stream()
                .sorted(Comparator.comparingInt(level -> level.getLevelNumber()))
                .flatMap(level -> level.getSpots().stream())
                .filter(spot -> !spot.isOccupied())
                .filter(spot -> Arrays.asList(allowedTypes).contains(spot.getSpotType()))
                .findFirst();
    }
}
