package org.sunbong.allmart_api.elasticsearch;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class ElasticSearchService {

    private final RestTemplate restTemplate = new RestTemplate(); // RestTemplate 인스턴스 생성

    // 상품 검색
    public List<Map<String, Object>> fetchSearchResults(String keyword) {
        String url = "http://127.0.0.1:8000/search?query=" + keyword;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();
            return (List<Map<String, Object>>) body.get("results");
        }
        throw new RuntimeException("Failed to fetch search results");
    }

    // 상품 인덱싱(저장)
    public void indexProduct(String name, String sku) {
        String url = "http://127.0.0.1:8000/index"; // FastAPI 서버 URL

        // 요청 데이터 구성
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("sku", sku);

        // HTTP 요청
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully indexed product '{}' in Elasticsearch", name);
            } else {
                log.error("Failed to index product '{}' in Elasticsearch. Response: {}", name, response.getBody());
            }
        } catch (Exception e) {
            log.error("Exception occurred while indexing product '{}' in Elasticsearch: {}", name, e.getMessage(), e);
            throw new RuntimeException("Error indexing product in Elasticsearch: " + e.getMessage(), e);
        }
    }
}
