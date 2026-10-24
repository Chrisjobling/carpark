package com.tds.carpark.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VehicleDetails(@NotBlank(message = "vehicleReg is required")
                             String vehicleReg,
                             @NotNull(message = "vehicleType is required")
                             @Min(value = 1, message = "vehicleType must be 1, 2 or 3")
                             @Max(value = 3, message = "vehicleType must be 1, 2 or 3")
                             Integer vehicleType) {
}
