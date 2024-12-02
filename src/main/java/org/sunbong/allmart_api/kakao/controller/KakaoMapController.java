package org.sunbong.allmart_api.kakao.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sunbong.allmart_api.kakao.dto.MartLocationDTO;
import org.sunbong.allmart_api.kakao.service.KakaoMapService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kakao")
public class KakaoMapController {

    private final KakaoMapService kakaoMapService;

}
