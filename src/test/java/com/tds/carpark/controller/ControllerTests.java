package com.tds.carpark.controller;


import com.tds.carpark.model.ParkingAvailability;
import com.tds.carpark.model.VehicleDetails;
import com.tds.carpark.model.VehicleParkedDetails;
import com.tds.carpark.model.VehicleRegistration;
import com.tds.carpark.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ParkingController.class)
@AutoConfigureRestTestClient
class ControllerTests {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private VehicleService vehicleService;

    @Test
    void shouldReturnParkingAvailability() {
        when(vehicleService.getSpaces())
                .thenReturn(new ParkingAvailability(50, 0));

        restTestClient.get().uri("/parking")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.availableSpaces").isEqualTo(50)
                .jsonPath("$.occupiedSpaces").isEqualTo(0);
    }

    @Test
    void shouldParkCar(){
        when(vehicleService.parkVehicle(any())).thenReturn(new VehicleParkedDetails("TESTREG", 1,
                Instant.parse("2026-10-23T16:48:00Z")));

        restTestClient.post().uri("/parking").body(new VehicleDetails("TESTREG",1))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.vehicleReg").isEqualTo("TESTREG")
                .jsonPath("$.timeIn").isEqualTo("2026-10-23T16:48:00Z")
                .jsonPath("$.vehicleType").doesNotExist();

    }

    @Test
    void shouldRejectParkingWithInvalidNumberPlate() {
        restTestClient.post().uri("/parking").body(new VehicleDetails("TESTREGTOOLONG", 1))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.vehicleReg").isEqualTo("vehicleReg must be between 1 to 7 characters");
    }


    @Test
    void shouldRejectParkingWithInvalidVehicleType() {
        restTestClient.post().uri("/parking").body(new VehicleDetails("TESTREG", 4))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.vehicleType").isEqualTo("vehicleType must be 1, 2 or 3");
    }

    @Test
    void shouldRejectParkingWithMissingFields() {
        restTestClient.post().uri("/parking").body(Map.of())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.vehicleReg").isEqualTo("vehicleReg is required")
                .jsonPath("$.vehicleType").isEqualTo("vehicleType is required");
    }

    @Test
    void shouldRejectBillWithBlankRegistration() {
        restTestClient.post().uri("/parking/bill").body(new VehicleRegistration(" "))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.vehicleReg").isEqualTo("vehicleReg is required");
    }
}