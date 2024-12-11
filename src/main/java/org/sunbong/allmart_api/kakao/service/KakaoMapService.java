package org.sunbong.allmart_api.kakao.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.kakao.dto.MartLocationDTO;
import org.sunbong.allmart_api.kakao.dto.MartMapDTO;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.dto.MartListDTO;
import org.sunbong.allmart_api.mart.repository.MartRepository;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class KakaoMapService {

    @Value("${kakao.KAKAO_GEO_API_KEY}")
    private String KAKAO_GEO_API_KEY;
    @Value("${kakao.KAKAO_MAP_API_KEY}")
    private String KAKAO_MAP_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MartRepository martRepository;

    // 리스트
    public PageResponseDTO<MartListDTO> list(PageRequestDTO pageRequestDTO, double lat, double lng) {

        PageResponseDTO<MartListDTO> result = martRepository.listWithinRadius(pageRequestDTO, lat, lng, 3.0);

        return result;
    }

    // Kakao 지도 스크립트 URL 반환
    public String getMapScriptUrl() {
        // API 키를 Base64로 인코딩
        String encodedApiKey = Base64.getEncoder().encodeToString(KAKAO_MAP_API_KEY.getBytes());

        // 인코딩된 키를 포함한 스크립트 URL 생성
        String scriptUrl = "//dapi.kakao.com/v2/maps/sdk.js?appkey=" + encodedApiKey + "&autoload=false";
        log.info("Generated Kakao Map Script URL: {}", scriptUrl);
        return scriptUrl;
    }

    // 마트 데이터를 Kakao 지도 마커 정보로 변환
    public List<MartMapDTO> getMartMapData(double lat, double lng) {
        List<Mart> marts = martRepository.findAll();
        double radius = 3.0; // 반경 3km

        List<MartMapDTO> result = marts.stream()
                .filter(mart -> calculateDistance(lat, lng, mart.getLat(), mart.getLng()) <= radius)
                .map(mart -> MartMapDTO.builder()
                        .martID(mart.getMartID())
                        .martName(mart.getMartName())
                        .lat(mart.getLat())
                        .lng(mart.getLng())
                        .build())
                .collect(Collectors.toList());

        // 결과 로그 출력
        log.info("Filtered MartMapDTO List: {}", result);

        return result;
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 6371; // 지구 반경 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    // 주소를 위도 경도로 변환
    public MartLocationDTO getCoordinates(String address) {

        log.info("=================================");
        log.info(KAKAO_GEO_API_KEY);

        try {

            String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

            log.info("========================");
            log.info(apiUrl);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + KAKAO_GEO_API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            // 응답 파싱
            return parseResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Kakao API", e);
        }
    }

    // 주소를 위도 경도로 변환한 것 응답 파싱
    private MartLocationDTO parseResponse(String response) {
        try {
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            // documents 배열 확인
            JsonNode documents = root.path("documents");
            if (documents.isEmpty()) {
                throw new RuntimeException("No address found for the given query.");
            }

            // 첫 번째 결과 추출
            JsonNode document = documents.get(0);
            String name = document.path("address_name").asText();
            double x = document.path("x").asDouble(); // 경도 (Longitude)
            double y = document.path("y").asDouble(); // 위도 (Latitude)

            // DTO 생성 (Builder 사용)
            return MartLocationDTO.builder()
                    .addressName(name)
                    .x(x)
                    .y(y)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Kakao API response", e);
        }
    }

}
