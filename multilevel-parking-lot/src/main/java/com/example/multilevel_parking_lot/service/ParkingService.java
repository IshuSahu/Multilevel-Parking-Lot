package com.example.multilevel_parking_lot.service;

import com.example.multilevel_parking_lot.dto.ParkRequest;
import com.example.multilevel_parking_lot.dto.ParkResponse;
import com.example.multilevel_parking_lot.dto.UnparkResponse;

public interface ParkingService {
    ParkResponse park(ParkRequest request);
    UnparkResponse unpark(String ticketId);
}
