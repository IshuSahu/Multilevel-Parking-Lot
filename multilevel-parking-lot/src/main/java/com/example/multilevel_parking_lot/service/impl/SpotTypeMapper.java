package com.example.multilevel_parking_lot.service.impl;

import com.example.multilevel_parking_lot.model.enums.VehicleType;

public final class SpotTypeMapper {
    public static String[] typesForVehicle(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> new String[] {"MOTORCYCLE"};
            case CAR -> new String[] {"COMPACT", "REGULAR", "EV", "LARGE"};
            case TRUCK -> new String[] {"LARGE"};
            case EV -> new String[] {"EV", "REGULAR", "COMPACT"};
            default -> new String[] {"REGULAR"};
        };
    }
}
