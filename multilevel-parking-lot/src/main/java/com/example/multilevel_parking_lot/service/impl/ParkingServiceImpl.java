package com.example.multilevel_parking_lot.service.impl;

import com.example.multilevel_parking_lot.dto.ParkRequest;
import com.example.multilevel_parking_lot.dto.ParkResponse;
import com.example.multilevel_parking_lot.dto.UnparkResponse;
import com.example.multilevel_parking_lot.exception.NotFoundException;
import com.example.multilevel_parking_lot.model.ParkingLot;
import com.example.multilevel_parking_lot.model.ParkingSpot;
import com.example.multilevel_parking_lot.model.ParkingTicket;
import com.example.multilevel_parking_lot.model.Vehicle;
import com.example.multilevel_parking_lot.model.enums.TicketStatus;
import com.example.multilevel_parking_lot.repository.ParkingLotRepository;
import com.example.multilevel_parking_lot.service.ParkingService;
import com.example.multilevel_parking_lot.service.allocation.SpotAllocationStrategy;
import com.example.multilevel_parking_lot.service.fee.FeeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {

    private final ParkingLotRepository parkingLotRepository;
    private final SpotAllocationStrategy allocationStrategy;
    private final FeeStrategy feeStrategy;

    // in-memory ticket store; replace with DB table in production
    private final Map<String, ParkingTicket> ticketStore = new ConcurrentHashMap<>();

    @Override
    public ParkResponse park(ParkRequest request) {
        ParkingLot lot = parkingLotRepository.findById(request.getParkingLotId())
                .orElseThrow(() -> new NotFoundException("Parking lot not found"));

        Vehicle vehicle = new Vehicle(request.getPlateNumber(), request.getVehicleType());

        synchronized (lot.getLock()) {
            var spotOpt = allocationStrategy.allocate(lot, vehicle);
            if (spotOpt.isEmpty()) {
                return ParkResponse.builder()
                        .success(false)
                        .message("No spot available")
                        .build();
            }
            ParkingSpot spot = spotOpt.get();
            String ticketId = UUID.randomUUID().toString();
            ParkingTicket ticket = ParkingTicket.builder()
                    .ticketId(ticketId)
                    .parkingLotId(lot.getId())
                    .spotId(spot.getId())
                    .vehiclePlate(vehicle.getPlateNumber())
                    .vehicleType(vehicle.getVehicleType().name())
                    .entryTime(Instant.now())
                    .status(TicketStatus.ACTIVE)
                    .build();

            boolean occupied = spot.occupy(ticketId);
            if (!occupied) {
                // someone took it concurrently - try again recursively or return failure
                return ParkResponse.builder().success(false).message("Race condition - try again").build();
            }

            ticketStore.put(ticketId, ticket);
            lot.getTicketIndex().put(ticketId, ticket);

            return ParkResponse.builder()
                    .success(true)
                    .ticketId(ticketId)
                    .spotId(spot.getId())
                    .entryTime(ticket.getEntryTime())
                    .message("Parked successfully")
                    .build();
        }
    }

    @Override
    public UnparkResponse unpark(String ticketId) {
        ParkingTicket ticket = ticketStore.get(ticketId);
        if (ticket == null) throw new NotFoundException("Ticket not found");

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            return UnparkResponse.builder().success(false).message("Already closed").build();
        }
        ParkingLot lot = parkingLotRepository.findById(ticket.getParkingLotId())
                .orElseThrow(() -> new NotFoundException("Parking lot not found"));

        synchronized (lot.getLock()) {
            ParkingSpot spot = lot.getSpotIndex().get(ticket.getSpotId());
            if (spot == null) throw new NotFoundException("Spot not found");
            // compute duration and fee
            Instant exit = Instant.now();
            Duration duration = Duration.between(ticket.getEntryTime(), exit);
            BigDecimal cost = feeStrategy.calculate(ticket, duration);

            // free spot
            spot.free();

            ticket.setExitTime(exit);
            ticket.setCost(cost);
            ticket.setStatus(TicketStatus.CLOSED);
            ticketStore.put(ticketId, ticket);

            lot.getTicketIndex().remove(ticketId);

            return UnparkResponse.builder()
                    .success(true)
                    .ticketId(ticketId)
                    .cost(cost)
                    .durationMinutes(duration.toMinutes())
                    .message("Unparked successfully")
                    .build();
        }
    }
}
