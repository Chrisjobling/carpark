package com.tds.carpark.service;

import com.tds.carpark.model.ParkingAvailability;
import com.tds.carpark.model.ParkingBill;
import com.tds.carpark.model.VehicleDetails;
import com.tds.carpark.model.VehicleParkedDetails;
import com.tds.carpark.model.VehicleRegistration;
import com.tds.carpark.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTests {

    private static final Instant ARRIVAL = Instant.parse("2026-10-24T10:00:00Z");

    private final VehicleRepository repository = new VehicleRepository();
    private final ParkingChargeCalculator calculator = new ParkingChargeCalculator();
    private final VehicleService service = serviceAt(ARRIVAL, 50);

    private VehicleService serviceAt(Instant now, int capacity) {
        return new VehicleService(repository, capacity, calculator, Clock.fixed(now, ZoneOffset.UTC));
    }

    @Test
    void reportsAllSpacesAvailableWhenEmpty() {
        assertEquals(new ParkingAvailability(50, 0), service.getSpaces());
    }

    @Test
    void reportsAvailabilityAfterParking() {
        service.parkVehicle(new VehicleDetails("AB12 CDE", 1));

        assertEquals(new ParkingAvailability(49, 1), service.getSpaces());
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
        VehicleService singleSpaceService = serviceAt(ARRIVAL, 1);
        singleSpaceService.parkVehicle(new VehicleDetails("AB12 CDE", 1));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> singleSpaceService.parkVehicle(new VehicleDetails("FG34 HIJ", 1)));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Car park is full", exception.getReason());
        assertEquals(1, repository.getVehicleCount());
        assertTrue(repository.findByRegistration("FG34 HIJ").isEmpty());
        assertEquals(new ParkingAvailability(0, 1), singleSpaceService.getSpaces());
    }

    @Test
    void rejectsParkingWhenCapacityIsZero() {
        VehicleService closedService = serviceAt(ARRIVAL, 0);

        assertThrows(ResponseStatusException.class,
                () -> closedService.parkVehicle(new VehicleDetails("AB12 CDE", 1)));

        assertEquals(0, repository.getVehicleCount());
    }

    @Test
    void rejectsInvalidVehicleType() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.parkVehicle(new VehicleDetails("AB12 CDE", 4)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(0, repository.getVehicleCount());
    }

    @Test
    void returnsNotFoundWhenBillingUnknownVehicle() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.generateParkingBill(new VehicleRegistration("UNKNOWN")));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void billsParkedVehicle() {
        service.parkVehicle(new VehicleDetails("AB12 CDE", 2));

        ParkingBill bill = service.generateParkingBill(new VehicleRegistration("AB12 CDE"));

        assertEquals("AB12 CDE", bill.vehicleReg());
        assertEquals(0.0, bill.vehicleCharge(), 0.0001);
    }

    @Test
    void billsVehicleForTimeParked() {
        service.parkVehicle(new VehicleDetails("AB12 CDE", 3));
        VehicleService later = serviceAt(ARRIVAL.plus(Duration.ofMinutes(12)), 50);

        ParkingBill bill = later.generateParkingBill(new VehicleRegistration("AB12 CDE"));

        assertEquals(6.80, bill.vehicleCharge(), 0.0001);
        assertEquals(ARRIVAL, bill.timeIn());
        assertEquals(ARRIVAL.plus(Duration.ofMinutes(12)), bill.timeOut());
    }
}
