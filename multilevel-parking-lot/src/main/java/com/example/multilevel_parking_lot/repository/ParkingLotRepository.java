package com.example.multilevel_parking_lot.repository;

import com.example.multilevel_parking_lot.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, String> {
}
