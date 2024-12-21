package org.sunbong.allmart_api.inventory.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.inventory.dto.InventoryDTO;
import org.sunbong.allmart_api.inventory.dto.InventoryListDTO;
import org.sunbong.allmart_api.inventory.service.InventoryService;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("list")
    public ResponseEntity<PageResponseDTO<InventoryListDTO>> list(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<InventoryListDTO> response = inventoryService.list(pageRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateInventory(@PathVariable Long id, @RequestBody InventoryListDTO inventoryListDTO) throws Exception {
        log.info("InventoryController - updateInventory called with id: {} and body: {}", id, inventoryListDTO);

        Long updatedId = inventoryService.update(id, inventoryListDTO);

        return ResponseEntity.ok(updatedId);
    }


}
