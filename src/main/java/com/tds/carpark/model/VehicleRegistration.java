package com.tds.carpark.model;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record VehicleRegistration(@NotBlank(message = "vehicleReg is required") @Length(max=7, message = "vehicleReg must be between 1 to 7 characters")
                                  String vehicleReg) {
}
