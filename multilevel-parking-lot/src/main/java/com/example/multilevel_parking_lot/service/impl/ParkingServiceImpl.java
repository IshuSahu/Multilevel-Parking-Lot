package com.example.multilevel_parking_lot.service.impl;

import com.example.multilevel_parking_lot.dto.ParkRequest;
import com.example.multilevel_parking_lot.dto.ParkResponse;
import com.example.multilevel_parking_lot.dto.UnparkResponse;
import com.example.multilevel_parking_lot.exception.NotFoundException;
import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.ParkingSpot;
import com.example.multilevel_parking_lot.model.ParkingTicket;
import com.example.multilevel_parking_lot.repository.ParkingLotRepository;
import com.example.multilevel_parking_lot.repository.ParkingSpotRepository;
import com.example.multilevel_parking_lot.repository.ParkingTicketRepository;
import com.example.multilevel_parking_lot.service.ParkingService;
import com.example.multilevel_parking_lot.service.fee.FeeStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingTicketRepository parkingTicketRepository;
    private final FeeStrategy feeStrategy;

    // Park vehicle: transactional and uses atomic DB update to occupy spot
    @Override
    @Transactional
    public ParkResponse park(ParkRequest request) {
        ParkingLot lot = parkingLotRepository.findById(request.getParkingLotId())
                .orElseThrow(() -> new NotFoundException("Parking lot not found"));

        // determine acceptable spot types (string names) from vehicle type
        String[] types = SpotTypeMapper.typesForVehicle(request.getVehicleType());

        Optional<ParkingSpot> optSpot = parkingSpotRepository.findFirstAvailableByLotAndTypes(lot.getId(), types);
        if (optSpot.isEmpty()) {
            return ParkResponse.builder().success(false).message("No spot available").build();
        }

        ParkingSpot spot = optSpot.get();
        String ticketId = UUID.randomUUID().toString();

        // attempt atomic occupy
        int updated = parkingSpotRepository.occupyIfFree(spot.getId(), ticketId);
        if (updated == 0) {
            // race - someone else took it; fail fast (could retry a few times)
            return ParkResponse.builder().success(false).message("Race condition: try again").build();
        }

        // create & persist ticket
        ParkingTicket ticket = new ParkingTicket();
        ticket.setTicketId(ticketId);
        ticket.setParkingLotId(lot.getId());
        ticket.setSpotId(spot.getId());
        ticket.setVehiclePlate(request.getPlateNumber());
        ticket.setVehicleType(request.getVehicleType().name());
        ticket.setEntryTime(Instant.now());
        ticket.setStatus("ACTIVE");

        parkingTicketRepository.save(ticket);

        return ParkResponse.builder()
                .success(true)
                .ticketId(ticketId)
                .spotId(spot.getId())
                .entryTime(ticket.getEntryTime())
                .message("Parked successfully")
                .build();
    }

    // Unpark: transactional, free spot atomically and compute fee
    @Override
    @Transactional
    public UnparkResponse unpark(String ticketId) {
        ParkingTicket ticket = parkingTicketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        if ("CLOSED".equalsIgnoreCase(ticket.getStatus())) {
            return UnparkResponse.builder().success(false).message("Already closed").build();
        }

        // compute duration and fee
        Instant exit = Instant.now();
        Duration duration = Duration.between(ticket.getEntryTime(), exit);

        BigDecimal cost = feeStrategy.calculate(ticket, duration); // fee strategy uses ticket.getVehicleType()

        // free spot atomically: ensure only the owner ticket can free
        int freed = parkingSpotRepository.freeIfOwned(ticket.getSpotId(), ticketId);
        if (freed == 0) {
            // either already freed or mismatch - log and return error
            throw new IllegalStateException("Unable to free spot - ownership mismatch or already freed");
        }

        ticket.setExitTime(exit);
        ticket.setCost(cost);
        ticket.setStatus("CLOSED");
        parkingTicketRepository.save(ticket);

        return UnparkResponse.builder()
                .success(true)
                .ticketId(ticketId)
                .cost(cost)
                .durationMinutes(duration.toMinutes())
                .message("Unparked successfully")
                .build();
    }
}
