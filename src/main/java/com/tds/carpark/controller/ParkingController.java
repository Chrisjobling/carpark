package com.tds.carpark.controller;

import com.tds.carpark.model.ParkingAvailabilityResponse;
import com.tds.carpark.model.VehicleDetails;
import com.tds.carpark.model.VehicleParkedDetails;
import com.tds.carpark.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingController {

    private final VehicleService vehicleService;

    public ParkingController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/parking")
    public ResponseEntity<ParkingAvailabilityResponse> getSpaces(){
        return ResponseEntity.ok(vehicleService.getSpaces());
    }

    @PostMapping("/parking")
    public ResponseEntity<VehicleParkedDetails> addCar(@RequestBody VehicleDetails vehicleDetails){
        return ResponseEntity.ok(vehicleService.parkVehicle(vehicleDetails));
    }

}
