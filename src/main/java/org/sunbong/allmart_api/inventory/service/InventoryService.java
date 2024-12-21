package org.sunbong.allmart_api.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.inventory.domain.Inventory;
import org.sunbong.allmart_api.inventory.dto.InventoryDTO;
import org.sunbong.allmart_api.inventory.dto.InventoryListDTO;
import org.sunbong.allmart_api.inventory.repository.InventoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public PageResponseDTO<InventoryListDTO> list(PageRequestDTO pageRequestDTO) {

        log.info("InventoryService - list called with PageRequestDTO: {}", pageRequestDTO);

        return inventoryRepository.searchInventory(pageRequestDTO);
    }

    // 수정
    public Long update(Long id, InventoryListDTO dto) throws Exception {
        // 재고 ID로 재고를 조회, 없으면 예외 발생
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid inventory ID"));

        // DTO의 데이터를 업데이트
        inventory = inventory.toBuilder()
                .quantity(dto.getQuantity())
                .build();

        // 업데이트된 데이터를 저장
        Inventory updatedInventory = inventoryRepository.save(inventory);

        // 저장된 재고 ID 반환
        return updatedInventory.getInventoryID();
    }

}
