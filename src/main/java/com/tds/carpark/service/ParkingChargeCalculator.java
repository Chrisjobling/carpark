package com.tds.carpark.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Component
public class ParkingChargeCalculator {

    private static final int SURCHARGE_INTERVAL_MINUTES = 5;
    private static final int SURCHARGE_IN_PENCE = 100;

    public double calculateCharge(Duration timeParked, int vehicleType) {
        long minutes = timeParked.toMinutes();
        long chargeInPence = minutes * perMinuteRateInPence(vehicleType)
                + (minutes / SURCHARGE_INTERVAL_MINUTES) * SURCHARGE_IN_PENCE;
        return chargeInPence / 100.0;
    }

    public void validateVehicleType(int vehicleType) {
        perMinuteRateInPence(vehicleType);
    }

    private int perMinuteRateInPence(int vehicleType) {
        return switch (vehicleType) {
            case 1 -> 10;
            case 2 -> 20;
            case 3 -> 40;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Vehicle type must be 1, 2 or 3");
        };
    }
}
