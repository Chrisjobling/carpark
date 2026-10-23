package com.tds.carpark.controller;


import com.tds.carpark.model.ParkingAvailabilityResponse;
import com.tds.carpark.model.VehicleDetails;
import com.tds.carpark.model.VehicleParkedDetails;
import com.tds.carpark.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.Instant;

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
                .thenReturn(new ParkingAvailabilityResponse(50, 0));

        restTestClient.get().uri("/parking")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.availableSpaces").isEqualTo(50)
                .jsonPath("$.occupiedSpaces").isEqualTo(0);
    }

    @Test
    void shouldParkCar(){
        when(vehicleService.parkVehicle(any())).thenReturn(new VehicleParkedDetails("VEHICLE_REG",
                Instant.parse("2026-10-23T16:48:00Z")));

        restTestClient.post().uri("/parking").body(new VehicleDetails("VEHICLE_REG",1))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.vehicleReg").isEqualTo("VEHICLE_REG")
                .jsonPath("$.timeIn").isEqualTo("2026-10-23T16:48:00Z");

    }
}