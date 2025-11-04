package com.example.multilevel_parking_lot.model;

import com.example.multilevel_parking_lot.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    private String plateNumber;
    private VehicleType vehicleType;
}
