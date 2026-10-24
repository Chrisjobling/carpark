package com.tds.carpark.model;

import jakarta.validation.constraints.NotBlank;

public record VehicleRegistration(@NotBlank(message = "vehicleReg is required")
                                  String vehicleReg) {
}
