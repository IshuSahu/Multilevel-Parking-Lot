package com.example.multilevel_parking_lot.service.allocation;

import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.ParkingSpot;
import com.example.multilevel_parking_lot.model.Vehicle;

import java.util.Optional;

public interface SpotAllocationStrategy {
    Optional<ParkingSpot> allocate(ParkingLot parkingLot, Vehicle vehicle);

}
