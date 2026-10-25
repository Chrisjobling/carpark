package com.tds.carpark.service;

import com.tds.carpark.model.*;
import com.tds.carpark.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class VehicleService {

    Logger logger = LoggerFactory.getLogger(VehicleService.class);


    private final int carparkCapacity;
    private final VehicleRepository vehicleRepository;
    private final ParkingChargeCalculator chargeCalculator;
    private final Clock clock;

    public VehicleService(
            VehicleRepository vehicleRepository,
            @Value("${carpark.capacity}") int carparkCapacity,
            ParkingChargeCalculator chargeCalculator,
            Clock clock) {
        this.vehicleRepository = vehicleRepository;
        this.carparkCapacity = carparkCapacity;
        this.chargeCalculator = chargeCalculator;
        this.clock = clock;
    }

    public ParkingAvailability getSpaces() {
        int occupiedSpaces = vehicleRepository.getVehicleCount();
        return new ParkingAvailability(calculateAvailableSpaces(carparkCapacity,occupiedSpaces), occupiedSpaces);
    }

    public synchronized VehicleParkedDetails parkVehicle(VehicleDetails vehicleDetails) {
        chargeCalculator.validateVehicleType(vehicleDetails.vehicleType());
        if (vehicleRepository.getVehicleCount() >= carparkCapacity) {
            logger.warn("Car Park Is full");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Car park is full");
        }
        VehicleParkedDetails parkedDetails = new VehicleParkedDetails(
                vehicleDetails.vehicleReg(), vehicleDetails.vehicleType(), Instant.now(clock));
        if (!vehicleRepository.saveIfAbsent(parkedDetails)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle is already parked");
        }
        logger.info("Vehicle Parked, Numberplate : {} , time {}", vehicleDetails.vehicleReg(), parkedDetails.timeIn());
        return parkedDetails;
    }

    public ParkingBill generateParkingBill(VehicleRegistration vehicleReg){
        VehicleParkedDetails vehicle = vehicleRepository.removeByRegistration(vehicleReg.vehicleReg())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        Instant timeOut = Instant.now(clock);
        Duration timeParked = Duration.between(vehicle.timeIn(), timeOut);

        return new ParkingBill(vehicle.vehicleReg(),
                chargeCalculator.calculateCharge(timeParked, vehicle.vehicleType()), vehicle.timeIn(), timeOut);
    }

    private int calculateAvailableSpaces(int carparkCapacity, int occupiedSpaces){
        return carparkCapacity - occupiedSpaces;
    }
}
