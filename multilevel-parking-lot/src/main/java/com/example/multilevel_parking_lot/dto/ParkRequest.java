package com.example.multilevel_parking_lot.dto;

import com.example.multilevel_parking_lot.model.enums.VehicleType;
import lombok.Data;

@Data
public class ParkRequest {
    private String parkingLotId;
    private String plateNumber;
    private VehicleType vehicleType;
}
