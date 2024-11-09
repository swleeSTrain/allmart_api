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
    @GetMapping("/{productID}")
    public ResponseEntity<ProductReadDTO> readById(
            @PathVariable("productID") Long productID
    ) {
        log.info("=======Product ReadById: {} =======", productID);

        return ResponseEntity.ok(productService.readById(productID));
    }

    // 목록
    @GetMapping("list")
    public ResponseEntity<PageResponseDTO<ProductListDTO>> list(
            @Validated PageRequestDTO pageRequestDTO
    ) {
        log.info("=======Product List=======");

        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return ResponseEntity.ok(productService.list(pageRequestDTO));
    }


    // 등록
    @PostMapping(value = "add", consumes = { "multipart/form-data" })
    public ResponseEntity<Long> register(
            @ModelAttribute ProductAddDTO dto
    ) throws Exception {

        log.info("=======Product Register=======");

        Long qno = productService.register(dto);

        return ResponseEntity.ok(qno);
    }

    // 삭제
    @DeleteMapping("/{productID}")
    public ResponseEntity<Long> delete(@PathVariable Long productID) {

        log.info("=======Product Delete: {} =======", productID);

        Long deletedQno = productService.delete(productID);

        return ResponseEntity.ok(deletedQno);
    }

    // 수정
    @PutMapping("/{productID}")
    public ResponseEntity<Long> edit(
            @PathVariable Long productID,
            @ModelAttribute ProductEditDTO dto) throws Exception {

        log.info("=======Product Update: {} =======", productID);

        Long updatedID = productService.edit(productID, dto);

        return ResponseEntity.ok(updatedID);
    }
}
