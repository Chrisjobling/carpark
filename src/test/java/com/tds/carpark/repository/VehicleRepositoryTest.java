package com.tds.carpark.repository;

import com.tds.carpark.model.VehicleParkedDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRepositoryTest {
    VehicleRepository vehicleRepository;

    @BeforeEach
    public void init() {
        vehicleRepository = new VehicleRepository();
    }
    @Test
    void getVehicleCountShouldReturnZero() {
        assertEquals(0, vehicleRepository.getVehicleCount());
    }

    @Test
    void getVehicleCountShouldReturnTwo() {
        vehicleRepository.saveIfAbsent(new VehicleParkedDetails("ABCEFG",1, Instant.now()));
        vehicleRepository.saveIfAbsent(new VehicleParkedDetails("ABCEFGH",2, Instant.now()));
        assertEquals(2, vehicleRepository.getVehicleCount());
    }

    @Test
    void saveIfAbsentShouldReturnTrueIfCarIsNotParked() {
        assertTrue(vehicleRepository.saveIfAbsent(new VehicleParkedDetails(
                "ABCEFG",1, Instant.now())));
    }

    @Test
    void saveIfAbsentShouldReturnTrueIfCarIsAlreadyParked() {
        VehicleParkedDetails savedVehicle = new VehicleParkedDetails(
                "ABCEFG", 1, Instant.now());
        assertTrue(vehicleRepository.saveIfAbsent(savedVehicle));
        assertFalse(vehicleRepository.saveIfAbsent(savedVehicle));
    }

    @Test
    void findByRegistrationShouldReturnParkedVehicleDetailsWhenFound() {
        VehicleParkedDetails savedVehicle = new VehicleParkedDetails(
                "ABCEFG", 1, Instant.now());
        vehicleRepository.saveIfAbsent(savedVehicle);
        assertEquals(Optional.of(savedVehicle), vehicleRepository.findByRegistration(savedVehicle.vehicleReg()));
    }

    @Test
    void removeByRegistrationShouldReturnEmptyWhenNotPresent() {
       //TODO
    }

    @Test
    void removeByRegistrationShouldRemoveVehicleAndReturnParkedVehicleDetailsWhenFound() {
        //TODO
    }

}