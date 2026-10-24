package com.tds.carpark.controller;

import com.tds.carpark.model.*;
import com.tds.carpark.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
public class ParkingController {

    private final VehicleService vehicleService;

    public ParkingController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/parking")
    public ResponseEntity<ParkingAvailability> getSpaces(){
        return ResponseEntity.ok(vehicleService.getSpaces());
    }

    @PostMapping("/parking")
    public ResponseEntity<VehicleParkedDetails> addCar(@Valid @RequestBody VehicleDetails vehicleDetails){
        return ResponseEntity.ok(vehicleService.parkVehicle(vehicleDetails));
    }

    @PostMapping("/parking/bill")
    public ResponseEntity<ParkingBill> payBill(@Valid @RequestBody VehicleRegistration vehicleReg){
        return  ResponseEntity.ok(vehicleService.generateParkingBill(vehicleReg));

    }

}
