package com.example.multilevel_parking_lot.controller;

import com.example.multilevel_parking_lot.dto.ParkRequest;
import com.example.multilevel_parking_lot.dto.ParkResponse;
import com.example.multilevel_parking_lot.dto.UnparkResponse;
import com.example.multilevel_parking_lot.exception.NotFoundException;
import com.example.multilevel_parking_lot.model.Level;
import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.enums.SpotType;
import com.example.multilevel_parking_lot.repository.ParkingLotRepository;
import com.example.multilevel_parking_lot.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;
    private final ParkingLotRepository parkingLotRepository;

    @PostMapping("/park")
    public ResponseEntity<ParkResponse> park(@Valid @RequestBody ParkRequest request) {
        return ResponseEntity.ok(parkingService.park(request));
    }

    @PostMapping("/unpark/{ticketId}")
    public ResponseEntity<UnparkResponse> unpark(@PathVariable String ticketId) {
        return ResponseEntity.ok(parkingService.unpark(ticketId));
    }

    /**
     * Availability endpoint — read-only transactional so LAZY associations can be traversed safely.
     */
    @GetMapping("/{lotId}/availability")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> availability(@PathVariable String lotId) {
        ParkingLot lot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new NotFoundException("Parking lot not found"));

        Map<String, Object> out = new HashMap<>();
        out.put("lotId", lot.getId());
        out.put("name", lot.getName());

        List<Map<String,Object>> levels = new ArrayList<>();
        for (Level l : lot.getLevels()) {
            Map<String, Object> levelMap = new HashMap<>();
            levelMap.put("levelNumber", l.getLevelNumber());

            // total spots using JPA collection
            int totalSpots = (l.getSpots() == null) ? 0 : l.getSpots().size();
            levelMap.put("totalSpots", totalSpots);

            long availableSpots = 0L;
            if (l.getSpots() != null) {
                availableSpots = l.getSpots().stream()
                        .filter(s -> !s.isOccupied())
                        .count();
            }
            levelMap.put("availableSpots", availableSpots);

            // breakdown by spot type (spotType stored as String in entity)
            Map<String, Map<String, Object>> byType = new LinkedHashMap<>();
            for (SpotType st : SpotType.values()) {
                long totalByType = 0L;
                long availableByType = 0L;

                if (l.getSpots() != null) {
                    totalByType = l.getSpots().stream()
                            .filter(s -> st.name().equals(s.getSpotType()))
                            .count();

                    availableByType = l.getSpots().stream()
                            .filter(s -> st.name().equals(s.getSpotType()) && !s.isOccupied())
                            .count();
                }

                Map<String, Object> t = new HashMap<>();
                t.put("total", totalByType);
                t.put("available", availableByType);
                byType.put(st.name(), t);
            }
            levelMap.put("bySpotType", byType);
            levels.add(levelMap);
        }
        out.put("levels", levels);

        // overall summary
        long totalSpots = lot.getLevels().stream()
                .mapToInt(l -> (l.getSpots() == null) ? 0 : l.getSpots().size())
                .sum();
        long totalAvailable = lot.getLevels().stream()
                .mapToLong(l -> (l.getSpots() == null) ? 0L : l.getSpots().stream().filter(s -> !s.isOccupied()).count())
                .sum();
        out.put("summary", Map.of("totalSpots", totalSpots, "availableSpots", totalAvailable));

        return ResponseEntity.ok(out);
    }
}
