package com.example.multilevel_parking_lot.config;

import com.example.multilevel_parking_lot.model.Level;
import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.ParkingSpot;
import com.example.multilevel_parking_lot.repository.ParkingLotRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkingConfig {

    private final ParkingLotRepository parkingLotRepository;

    @PostConstruct
    public void init() {
        if (parkingLotRepository.count() > 0) return; // avoid duplicates

        ParkingLot lot = new ParkingLot();
        lot.setId("lot-1");
        lot.setName("Demo Parking Lot");

        // create 3 levels
        for (int levelNum = 1; levelNum <= 3; levelNum++) {
            Level level = new Level();
            level.setLevelNumber(levelNum);
            level.setParkingLot(lot);

            // create 10 spots per level
            for (int i = 1; i <= 10; i++) {
                ParkingSpot spot = new ParkingSpot();
                spot.setId("L" + levelNum + "-S" + i);
                spot.setNumber(i);
                spot.setLevel(level);
                spot.setSpotType((i % 2 == 0) ? "COMPACT" : "REGULAR");
                spot.setOccupied(false);

                level.getSpots().add(spot);
            }

            lot.getLevels().add(level);
        }

        parkingLotRepository.save(lot);
    }
}
