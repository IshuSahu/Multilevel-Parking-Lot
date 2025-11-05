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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;
    private final ParkingLotRepository parkingLotRepository;

    @PostMapping("/park")
    public ResponseEntity<ParkResponse> park(@RequestBody ParkRequest request) {
        return ResponseEntity.ok(parkingService.park(request));
    }

    @PostMapping("/unpark/{ticketId}")
    public ResponseEntity<UnparkResponse> unpark(@PathVariable String ticketId) {
        return ResponseEntity.ok(parkingService.unpark(ticketId));
    }

    @GetMapping("/{lotId}/availability")
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
            levelMap.put("totalSpots", l.totalSpots());
            levelMap.put("availableSpots", l.availableSpots());

            // breakdown by spot type
            Map<String, Map<String, Object>> byType = new LinkedHashMap<>();
            for (SpotType st : SpotType.values()) {
                long total = l.getSpots().values().stream()
                        .filter(s -> s.getSpotType() == st)
                        .count();
                long available = l.getSpots().values().stream()
                        .filter(s -> s.getSpotType() == st && s.isAvailable())
                        .count();

                Map<String, Object> t = new HashMap<>();
                t.put("total", total);
                t.put("available", available);
                byType.put(st.name(), t);
            }
            levelMap.put("bySpotType", byType);
            levels.add(levelMap);
        }
        out.put("levels", levels);

        // overall summary
        long totalSpots = lot.getLevels().stream().mapToInt(Level::totalSpots).sum();
        long totalAvailable = lot.getLevels().stream().mapToLong(Level::availableSpots).sum();
        out.put("summary", Map.of("totalSpots", totalSpots, "availableSpots", totalAvailable));

        return ResponseEntity.ok(out);
    }
}
