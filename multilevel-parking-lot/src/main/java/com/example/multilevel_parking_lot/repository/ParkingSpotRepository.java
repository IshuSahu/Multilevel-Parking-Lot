package com.example.multilevel_parking_lot.repository;

import com.example.multilevel_parking_lot.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, String> {

    // Native query to find the first available spot in a lot that matches spot_type list
    @Query(value = """
        SELECT ps.* FROM parking_spot ps
        JOIN level l ON ps.level_id = l.id
        WHERE ps.occupied = false
          AND l.parking_lot_id = :lotId
          AND ps.spot_type IN (:types)
        ORDER BY l.level_number ASC, ps.number ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<ParkingSpot> findFirstAvailableByLotAndTypes(@Param("lotId") String lotId, @Param("types") String[] types);

    // Attempt to occupy spot atomically; returns number of rows updated (1 = success)
    @Modifying
    @Query(value = "UPDATE parking_spot SET occupied = true, current_ticket_id = :ticketId WHERE id = :id AND occupied = false", nativeQuery = true)
    int occupyIfFree(@Param("id") String id, @Param("ticketId") String ticketId);

    @Modifying
    @Query(value = "UPDATE parking_spot SET occupied = false, current_ticket_id = NULL WHERE id = :id AND current_ticket_id = :ticketId", nativeQuery = true)
    int freeIfOwned(@Param("id") String id, @Param("ticketId") String ticketId);
}
