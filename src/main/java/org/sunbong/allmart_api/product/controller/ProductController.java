package org.sunbong.allmart_api.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.product.dto.ProductAddDTO;
import org.sunbong.allmart_api.product.dto.ProductEditDTO;
import org.sunbong.allmart_api.product.dto.ProductListDTO;
import org.sunbong.allmart_api.product.dto.ProductReadDTO;
import org.sunbong.allmart_api.product.service.ProductService;

@RestController
@RequestMapping("/api/v1/product")
@Log4j2
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 조회
    @GetMapping("/{martID}/{productID}")
    public ResponseEntity<ProductReadDTO> readById(
            @PathVariable("martID") Long martID,
            @PathVariable("productID") Long productID
    ) {
        log.info("=======Product ReadById: {} =======", productID);

        return ResponseEntity.ok(productService.readById(martID, productID));
    }

    // 목록
    @GetMapping("/{martID}/list")
    public ResponseEntity<PageResponseDTO<ProductListDTO>> list(
            @PathVariable("martID") Long martID,
            @Validated PageRequestDTO pageRequestDTO
    ) {
        log.info("=======Product List=======");

        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return ResponseEntity.ok(productService.list(martID, pageRequestDTO));
    }


    // 등록
    @PostMapping(value = "/{martID}/add", consumes = { "multipart/form-data" })
    public ResponseEntity<Long> register(
            @PathVariable("martID") Long martID,
            @ModelAttribute ProductAddDTO dto
    ) throws Exception {

        log.info("=======Product Register=======");

        Long id = productService.register(martID, dto);

        return ResponseEntity.ok(id);
    }

    // 삭제
    @DeleteMapping("/{martID}/{productID}")
    public ResponseEntity<Long> delete(
            @PathVariable("martID") Long martID,
            @PathVariable Long productID
    ) {

        log.info("=======Product Delete: {} =======", productID);

        Long deletedID = productService.delete(martID, productID);

        return ResponseEntity.ok(deletedID);
    }

    // 수정
    @PutMapping("/{martID}/{productID}")
    public ResponseEntity<Long> edit(
            @PathVariable("martID") Long martID,
            @PathVariable Long productID,
            @ModelAttribute ProductEditDTO dto) throws Exception {

        log.info("=======Product Update: {} =======", productID);

        Long updatedID = productService.edit(martID, productID, dto);

        return ResponseEntity.ok(updatedID);
    }
}
