package com.tds.carpark.model;

import java.time.Instant;

public record VehicleParkedDetails(String vehicleReg, Instant timeIn) {
}
