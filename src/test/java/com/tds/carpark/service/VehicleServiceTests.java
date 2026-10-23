package com.tds.carpark.service;

import com.tds.carpark.model.VehicleDetails;
import com.tds.carpark.model.ParkingAvailabilityResponse;
import com.tds.carpark.model.VehicleParkedDetails;
import com.tds.carpark.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTests {

    private final VehicleRepository repository = new VehicleRepository();
    private final VehicleService service = new VehicleService(repository, 50);

    @Test
    void reportsAllSpacesAvailableWhenEmpty() {
        assertEquals(new ParkingAvailabilityResponse(50, 0), service.getSpaces());
    }

    @Test
    void reportsAvailabilityAfterParking() {
        service.parkVehicle(new VehicleDetails("AB12 CDE", 1));

        assertEquals(new ParkingAvailabilityResponse(49, 1), service.getSpaces());
    }

    @Test
    void parksAndStoresVehicle() {
        VehicleParkedDetails parked = service.parkVehicle(new VehicleDetails("AB12 CDE", 1));

        assertEquals("AB12 CDE", parked.vehicleReg());
        assertEquals(parked, repository.findByRegistration("AB12 CDE").orElseThrow());
    }

    @Test
    void rejectsDuplicateRegistrationWithoutReplacingOriginal() {
        VehicleDetails vehicle = new VehicleDetails("AB12 CDE", 1);
        VehicleParkedDetails original = service.parkVehicle(vehicle);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.parkVehicle(vehicle));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(original, repository.findByRegistration("AB12 CDE").orElseThrow());
    }

    @Test
    void rejectsParkingWhenCapacityIsReached() {
        VehicleService singleSpaceService = new VehicleService(repository, 1);
        singleSpaceService.parkVehicle(new VehicleDetails("AB12 CDE", 1));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> singleSpaceService.parkVehicle(new VehicleDetails("FG34 HIJ", 1)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Car park is full", exception.getReason());
        assertEquals(1, repository.getVehicleCount());
        assertTrue(repository.findByRegistration("FG34 HIJ").isEmpty());
        assertEquals(new ParkingAvailabilityResponse(0, 1), singleSpaceService.getSpaces());
    }

    @Test
    void rejectsParkingWhenCapacityIsZero() {
        VehicleService closedService = new VehicleService(repository, 0);

        assertThrows(ResponseStatusException.class,
                () -> closedService.parkVehicle(new VehicleDetails("AB12 CDE", 1)));

        assertEquals(0, repository.getVehicleCount());
    }
}
