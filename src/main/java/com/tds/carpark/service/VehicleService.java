package com.tds.carpark.service;

import com.tds.carpark.model.*;
import com.tds.carpark.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;

@Service
public class VehicleService {

    private final int carparkCapacity;
    private final VehicleRepository vehicleRepository;

    public VehicleService(
            VehicleRepository vehicleRepository,
            @Value("${carpark.capacity}") int carparkCapacity) {
        this.vehicleRepository = vehicleRepository;
        this.carparkCapacity = carparkCapacity;
    }

    public ParkingAvailability getSpaces() {
        int occupiedSpaces = vehicleRepository.getVehicleCount();
        return new ParkingAvailability(calculateAvailableSpaces(carparkCapacity,occupiedSpaces), occupiedSpaces);
    }

    public synchronized VehicleParkedDetails parkVehicle(VehicleDetails vehicleDetails) {
        perMinuteRateInPence(vehicleDetails.vehicleType());
        if (vehicleRepository.getVehicleCount() >= carparkCapacity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Car park is full");
        }
        VehicleParkedDetails parkedDetails = new VehicleParkedDetails(
                vehicleDetails.vehicleReg(), vehicleDetails.vehicleType(), Instant.now());
        if (!vehicleRepository.saveIfAbsent(parkedDetails)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle is already parked");
        }
        return parkedDetails;
    }

    public ParkingBill generateParkingBill(VehicleRegistration vehicleReg){
        VehicleParkedDetails vehicle = vehicleRepository.findByRegistration(vehicleReg.vehicleReg())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        Instant timeOut = Instant.now();
        Duration timeParked = Duration.between(vehicle.timeIn(), timeOut);

        return new ParkingBill(vehicle.vehicleReg(),
                calculateVehicleCharge(timeParked, vehicle.vehicleType()), vehicle.timeIn(), timeOut);
    }

    private int calculateAvailableSpaces(int carparkCapacity, int occupiedSpaces){
        return carparkCapacity - occupiedSpaces;
    }

    static double calculateVehicleCharge(Duration timeParked, int vehicleType) {
        long minutes = timeParked.toMinutes();
        long chargeInPence = minutes * perMinuteRateInPence(vehicleType) + (minutes / 5) * 100;
        return chargeInPence / 100.0;
    }

    private static int perMinuteRateInPence(int vehicleType) {
        return switch (vehicleType) {
            case 1 -> 10;
            case 2 -> 20;
            case 3 -> 40;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Vehicle type must be 1, 2 or 3");
        };
    }
}
