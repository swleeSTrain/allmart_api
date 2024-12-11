package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.repository.DriverRepository;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    public void updateDriverDeliveryCount(Long driverId, int newDeliveryCount) {
        DriverEntity driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new IllegalArgumentException("Driver not found with id: " + driverId));
        driver = DriverEntity.builder()
            .driverId(driver.getDriverId())
            .name(driver.getName())
            .maxDeliveryCount(newDeliveryCount)
            .build();
        driverRepository.save(driver);
    }

    public DriverEntity addNewDriver(String name, int maxDeliveryCount) {
        DriverEntity newDriver = DriverEntity.builder()
            .name(name)
            .maxDeliveryCount(maxDeliveryCount)
            .build();
        return driverRepository.save(newDriver);
    }
}
