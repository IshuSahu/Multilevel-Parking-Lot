package com.example.multilevel_parking_lot.config;

import com.example.multilevel_parking_lot.model.Level;
import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.ParkingSpot;
import com.example.multilevel_parking_lot.model.enums.SpotType;
import com.example.multilevel_parking_lot.repository.ParkingLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ParkingConfig {
    private final ParkingLotRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        ParkingLot lot = new ParkingLot();
        lot.setId("lot-1");
        lot.setName("Demo Parking Lot");

        // create 3 levels
        for (int levelNo = 1; levelNo <= 3; levelNo++) {
            Level level = new Level();
            level.setId("L" + levelNo);
            level.setLevelNumber(levelNo);
            final int currentLevelNo = levelNo;

            IntStream.rangeClosed(1, 10).forEach(i -> {
                SpotType type = switch (i % 5) {
                    case 1 -> SpotType.MOTORCYCLE;
                    case 2 -> SpotType.COMPACT;
                    case 3 -> SpotType.REGULAR;
                    case 4 -> SpotType.EV;
                    default -> SpotType.LARGE;
                };

                ParkingSpot spot = ParkingSpot.builder()
                        .id(level.getId() + "-S" + i)
                        .level(currentLevelNo)
                        .number(i)
                        .spotType(type)
                        .build();

                level.getSpots().put(spot.getId(), spot);
            });

            lot.getLevels().add(level);
        }

        lot.indexSpots();
        repository.save(lot);
    }
}
