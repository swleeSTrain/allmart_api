package org.sunbong.allmart_api.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.delivery.domain.DeliveryEntity;
import org.sunbong.allmart_api.delivery.domain.DriverEntity;
import org.sunbong.allmart_api.delivery.dto.DriverListDTO;
import org.sunbong.allmart_api.delivery.repository.DeliveryRepository;
import org.sunbong.allmart_api.delivery.repository.DriverRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DriverService {

    private final DeliveryRepository deliveryRepository;
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

    /**
     * 모든 드라이버 리스트를 조회
     */
    public List<DriverListDTO> getAllDrivers() {
        List<DriverEntity> drivers = driverRepository.findAll();

        return drivers.stream()
                .map(driver -> DriverListDTO.builder()
                        .driverId(driver.getDriverId())
                        .name(driver.getName())
                        .maxDeliveryCount(driver.getMaxDeliveryCount())
                        .currentDeliveryCount(driver.getCurrentDeliveryCount())
                        .canAcceptDelivery(driver.canAcceptDelivery())
                        .build())
                .collect(Collectors.toList());
    }


}
