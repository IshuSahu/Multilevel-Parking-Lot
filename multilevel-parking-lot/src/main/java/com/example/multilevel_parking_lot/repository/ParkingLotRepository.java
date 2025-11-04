package com.example.multilevel_parking_lot.repository;

import com.example.multilevel_parking_lot.model.ParkingLot;

import java.util.Optional;

public interface ParkingLotRepository {
    Optional<ParkingLot> findById(String id);
    ParkingLot save(ParkingLot lot);
}
