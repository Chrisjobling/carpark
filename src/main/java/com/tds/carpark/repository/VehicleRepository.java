package com.tds.carpark.repository;

import com.tds.carpark.model.VehicleParkedDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class VehicleRepository {

    private final ConcurrentMap<String, VehicleParkedDetails> parkedVehicles = new ConcurrentHashMap<>();

    public int getVehicleCount() {
        return parkedVehicles.size();
    }

    public boolean saveIfAbsent(VehicleParkedDetails vehicle) {
        return parkedVehicles.putIfAbsent(vehicle.vehicleReg(), vehicle) == null;
    }

    public Optional<VehicleParkedDetails> findByRegistration(String vehicleReg) {
        return Optional.ofNullable(parkedVehicles.get(vehicleReg));
    }

    public Optional<VehicleParkedDetails> removeByRegistration(String vehicleReg) {
        return Optional.ofNullable(parkedVehicles.remove(vehicleReg));
    }
}
