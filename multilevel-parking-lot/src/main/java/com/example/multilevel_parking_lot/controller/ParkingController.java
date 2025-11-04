package com.example.multilevel_parking_lot.controller;

import com.example.multilevel_parking_lot.dto.ParkRequest;
import com.example.multilevel_parking_lot.dto.ParkResponse;
import com.example.multilevel_parking_lot.dto.UnparkResponse;
import com.example.multilevel_parking_lot.exception.NotFoundException;
import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.repository.ParkingLotRepository;
import com.example.multilevel_parking_lot.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<Map<String,Object>> availability(@PathVariable String lotId) {
        ParkingLot lot = parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new NotFoundException("lot not found"));
        Map<String,Object> out = new HashMap<>();
        out.put("lotId", lot.getId());
        out.put("levels", lot.getLevels().stream().map(l -> {
            Map<String,Object> m = new HashMap<>();
            m.put("level", l.getLevelNumber());
            m.put("total", l.totalSpots());
            m.put("available", l.availableSpots());
            return m;
        }).collect(Collectors.toList()));
        return ResponseEntity.ok(out);
    }
}
