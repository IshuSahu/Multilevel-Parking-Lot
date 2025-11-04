package com.example.multilevel_parking_lot.repository;

import com.example.multilevel_parking_lot.model.ParkingLot;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryParkingLotRepository implements ParkingLotRepository {
    private final Map<String, ParkingLot> store = new ConcurrentHashMap<>();

    @Override
    public Optional<ParkingLot> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public ParkingLot save(ParkingLot lot) {
        store.put(lot.getId(), lot);
        return lot;
    }
}
