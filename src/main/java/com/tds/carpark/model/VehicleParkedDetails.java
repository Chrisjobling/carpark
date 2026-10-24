package com.tds.carpark.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

public record VehicleParkedDetails(String vehicleReg, @JsonIgnore int vehicleType, Instant timeIn) {
}
