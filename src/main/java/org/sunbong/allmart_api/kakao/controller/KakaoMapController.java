package org.sunbong.allmart_api.kakao.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.kakao.dto.MartLocationDTO;
import org.sunbong.allmart_api.kakao.dto.MartMapDTO;
import org.sunbong.allmart_api.kakao.service.KakaoMapService;
import org.sunbong.allmart_api.mart.dto.MartListDTO;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kakao")
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;

    // 리스트
    @GetMapping("/marts/list")
    public ResponseEntity<PageResponseDTO<MartListDTO>> list(
            @Validated PageRequestDTO pageRequestDTO,
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        log.info("=======Mart List=======");

        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return ResponseEntity.ok(kakaoMapService.list(pageRequestDTO, lat, lng));
    }

    // Kakao 지도 스크립트 URL 반환
    @GetMapping("/script")
    public Map<String, String> getMapScriptUrl() {

        return Map.of("scriptUrl", kakaoMapService.getMapScriptUrl());
    }

    // Kakao 지도에 표시할 마트 데이터 반환
    @GetMapping("/marts")
    public List<MartMapDTO> getMartMapData(
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        return kakaoMapService.getMartMapData(lat, lng);
    }

    @GetMapping("/coordinates")
    public MartLocationDTO getCoordinates(@RequestParam String address) {

        return kakaoMapService.getCoordinates(address);
    }

}

