package org.sunbong.allmart_api.mart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.mart.dto.MartAddDTO;
import org.sunbong.allmart_api.mart.dto.MartListDTO;
import org.sunbong.allmart_api.mart.service.MartService;
import org.sunbong.allmart_api.product.dto.ProductAddDTO;
import org.sunbong.allmart_api.product.dto.ProductListDTO;

@RestController
@RequestMapping("/api/v1/mart")
@Log4j2
@RequiredArgsConstructor
public class MartController {

    private final MartService martService;

    // 목록
    @GetMapping("list")
    public ResponseEntity<PageResponseDTO<MartListDTO>> list(
            @Validated PageRequestDTO pageRequestDTO
    ) {
        log.info("=======Mart List=======");

        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return ResponseEntity.ok(martService.list(pageRequestDTO));
    }

    // 등록
    @PostMapping(value = "add", consumes = { "multipart/form-data" })
    public ResponseEntity<Long> register(
            @ModelAttribute MartAddDTO dto
    ) throws Exception {

        log.info("=======Mart Register=======");

        Long id = martService.register(dto);

        return ResponseEntity.ok(id);
    }


}
