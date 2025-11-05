package com.example.multilevel_parking_lot.dto;

import com.example.multilevel_parking_lot.model.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParkRequest {
    @NotBlank
    private String parkingLotId;
    @NotBlank
    private String plateNumber;
    @NotBlank
    private VehicleType vehicleType;
}
