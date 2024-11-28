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
import org.sunbong.allmart_api.mart.dto.MartEditDTO;
import org.sunbong.allmart_api.mart.dto.MartListDTO;
import org.sunbong.allmart_api.mart.service.MartService;

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

    // 소프트삭제
    @DeleteMapping("/{martID}")
    public ResponseEntity<Long> delete(@PathVariable Long martID) {

        log.info("=======Product Delete: {} =======", martID);

        Long deletedID = martService.delete(martID);

        return ResponseEntity.ok(deletedID);
    }

    // 수정
    @PutMapping("/{martID}")
    public ResponseEntity<Long> edit(
            @PathVariable Long martID,
            @ModelAttribute MartEditDTO dto) throws Exception {

        log.info("=======Product Update: {} =======", martID);

        Long updatedID = martService.edit(martID, dto);

        return ResponseEntity.ok(updatedID);
    }
}
