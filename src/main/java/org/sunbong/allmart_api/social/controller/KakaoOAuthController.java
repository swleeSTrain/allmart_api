package org.sunbong.allmart_api.social.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunbong.allmart_api.social.service.KakaoOAuthService;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/oauth/kakao")
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handleKakaoCallback(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String state = payload.get("state");
        String martID = payload.get("martID");

        log.info("code: " + code + ", state: " + state);
        if (code == null || state == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing code or state"));
        }

        // 카카오 서버와 통신하여 액세스 토큰 및 사용자 정보 가져오기
        Map<String, Object> authData = kakaoOAuthService.authenticateWithKakao(code, state, martID);

        return ResponseEntity.ok(authData);
    }
}
