package org.sunbong.allmart_api.kakao.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sunbong.allmart_api.kakao.dto.MartLocationDTO;
import org.sunbong.allmart_api.kakao.dto.MartMapDTO;
import org.sunbong.allmart_api.kakao.service.KakaoMapService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kakao")
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;

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
