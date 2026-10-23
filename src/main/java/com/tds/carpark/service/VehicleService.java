package com.tds.carpark.service;

import com.tds.carpark.model.ParkingAvailabilityResponse;
import com.tds.carpark.model.VehicleDetails;
import com.tds.carpark.model.VehicleParkedDetails;
import com.tds.carpark.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public ParkingAvailabilityResponse getSpaces() {
        int occupiedSpaces = vehicleRepository.getVehicleCount();
        return new ParkingAvailabilityResponse(calculateAvailableSpaces(carparkCapacity,occupiedSpaces), occupiedSpaces);
    }

    public synchronized VehicleParkedDetails parkVehicle(VehicleDetails vehicleDetails) {
        if (vehicleRepository.getVehicleCount() >= carparkCapacity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Car park is full");
        }
        VehicleParkedDetails parkedDetails =
                new VehicleParkedDetails(vehicleDetails.vehicleReg(), Instant.now());
        if (!vehicleRepository.saveIfAbsent(parkedDetails)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle is already parked");
        }
        return parkedDetails;
    }

    private int calculateAvailableSpaces(int carparkCapacity, int occupiedSpaces){
        return carparkCapacity - occupiedSpaces;
    }
}
