package com.example.multilevel_parking_lot.repository;

import com.example.multilevel_parking_lot.model.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, String> {
}
