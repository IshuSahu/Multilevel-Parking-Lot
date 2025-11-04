package com.example.multilevel_parking_lot.model.enums;

public enum SpotType {
    COMPACT,
    REGULAR,
    LARGE,
    MOTORCYCLE,
    EV;

    public boolean canFitVehicle(VehicleType vehicleType) {
        switch (this) {
            case MOTORCYCLE:
                return vehicleType == VehicleType.MOTORCYCLE;
            case COMPACT:
                return vehicleType == VehicleType.CAR || vehicleType == VehicleType.MOTORCYCLE;
            case REGULAR:
                return vehicleType == VehicleType.CAR || vehicleType == VehicleType.MOTORCYCLE || vehicleType == VehicleType.EV;
            case LARGE:
                return vehicleType == VehicleType.TRUCK || vehicleType == VehicleType.CAR;
            case EV:
                return vehicleType == VehicleType.EV || vehicleType == VehicleType.CAR;
            default:
                return false;
        }
    }
}
