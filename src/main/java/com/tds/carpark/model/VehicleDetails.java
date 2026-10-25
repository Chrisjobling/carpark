package com.tds.carpark.model;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record VehicleDetails(@NotBlank(message = "vehicleReg is required") @Length(max=7, message = "vehicleReg must be between 1 to 7 characters")
                             String vehicleReg,
                             @NotNull(message = "vehicleType is required")
                             @Min(value = 1, message = "vehicleType must be 1, 2 or 3")
                             @Max(value = 3, message = "vehicleType must be 1, 2 or 3")
                             Integer vehicleType) {
}
