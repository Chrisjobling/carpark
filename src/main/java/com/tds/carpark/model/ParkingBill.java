package com.tds.carpark.model;

import java.time.Instant;

public record ParkingBill(String vehicleReg, double vehicleCharge, Instant timeIn, Instant timeOut) { }
