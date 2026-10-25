package com.tds.carpark.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ParkingChargeCalculatorTest {

    private final ParkingChargeCalculator calculator = new ParkingChargeCalculator();

    @Test
    void chargesPerMinuteByVehicleType() {
        assertEquals(0.40, calculator.calculateCharge(Duration.ofMinutes(4), 1), 0.0001);
        assertEquals(0.80, calculator.calculateCharge(Duration.ofMinutes(4), 2), 0.0001);
        assertEquals(1.60, calculator.calculateCharge(Duration.ofMinutes(4), 3), 0.0001);
    }

    @Test
    void addsOnePoundForEveryFiveMinutes() {
        assertEquals(1.50, calculator.calculateCharge(Duration.ofMinutes(5), 1), 0.0001);
        assertEquals(4.00, calculator.calculateCharge(Duration.ofMinutes(10), 2), 0.0001);
        assertEquals(6.80, calculator.calculateCharge(Duration.ofMinutes(12), 3), 0.0001);
    }

    @Test
    void chargesNothingForUnderOneMinute() {
        assertEquals(0.0, calculator.calculateCharge(Duration.ofSeconds(59), 3), 0.0001);
    }

    @Test
    void rejectsInvalidVehicleType() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> calculator.validateVehicleType(4));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void acceptsValidVehicleTypes() {
        assertDoesNotThrow(() -> {
            calculator.validateVehicleType(1);
            calculator.validateVehicleType(2);
            calculator.validateVehicleType(3);
        });
    }
}
