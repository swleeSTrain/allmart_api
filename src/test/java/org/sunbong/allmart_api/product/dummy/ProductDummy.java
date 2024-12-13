//package org.sunbong.allmart_api.product.dummy;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.annotation.Commit;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//import org.sunbong.allmart_api.category.domain.Category;
//import org.sunbong.allmart_api.category.repository.CategoryRepository;
//import org.sunbong.allmart_api.common.util.CustomFileUtil;
//import org.sunbong.allmart_api.elasticsearch.ElasticSearchService;
//import org.sunbong.allmart_api.product.dto.ProductAddDTO;
//import org.sunbong.allmart_api.product.service.ProductService;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.io.File;
//
//
//import java.io.*;
//import java.math.BigDecimal;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@Transactional
//@Commit
//@Log4j2
//public class ProductDummy {
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private CustomFileUtil fileUtil;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private ElasticSearchService elasticSearchService;
//
//    @Test
//    public void testAddCategoriesInOrder() {
//        // 카테고리 이름 리스트 (순서대로)
//        List<String> categoryNames = List.of(
//                "과일", "채소", "쌀/잡곡/견과", "정육/계란류", "수산물/건해산",
//                "우유/유제품", "김치/반찬/델리", "생수/음료/주류",
//                "커피/원두/차", "면류/통조림", "양념/오일", "과자/간식",
//                "건강식품", "헤어/바디/뷰티", "청소/생활용품",
//                "주방용품", "생활잡화/공구", "반려동물"
//        );
//
//        // 카테고리 저장
//        for (String categoryName : categoryNames) {
//            if (!categoryRepository.findByName(categoryName).isPresent()) {
//                Category category = Category.builder()
//                        .name(categoryName)
//                        .build();
//
//                categoryRepository.save(category);
//                log.info(categoryName + " 카테고리가 생성되었습니다.");
//            } else {
//                log.info(categoryName + "는 이미 존재하는 카테고리입니다.");
//            }
//        }
//
//        // 검증
//        assertThat(categoryRepository.findAll().size()).isGreaterThanOrEqualTo(categoryNames.size());
//    }
//
//    @Test
//    @Rollback(value = false)
//    public void testForElasticSearch() throws Exception {
//        // JSON 파일 경로
//        String jsonFilePath = "src/test/resources/filtered_product_data.json";
//
//        // JSON 파일 읽기
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Map<String, Object>> productList = objectMapper.readValue(new File(jsonFilePath), List.class);
//
//        // 데이터 반복 처리
//        for (Map<String, Object> productData : productList) {
//            // JSON 데이터에서 값 추출
//            String name = (String) productData.get("name");
//            String sku = (String) productData.get("sku");
//            Integer price = (Integer) productData.get("price");
//            String fileUrl = (String) productData.get("file");
//            Long categoryID = ((Number) productData.get("categoryID")).longValue();
//            Long martID = ((Number) productData.get("martID")).longValue();
//
//            // MockMultipartFile 생성 (fileUrl에서 다운로드)
//            MultipartFile mockFile = createMockMultipartFileFromUrl(fileUrl);
//
//            // ProductAddDTO 생성
//            ProductAddDTO dto = ProductAddDTO.builder()
//                    .name(name)
//                    .sku(sku)
//                    .price(BigDecimal.valueOf(price))
//                    .files(List.of(mockFile))
//                    .categoryID(categoryID)
//                    .build();
//
//            // 상품 등록
//            Long productId = null;
//            try {
//                productId = productService.register(martID, dto); // martID 추가
//                assertNotNull(productId, "Product ID should not be null after registration");
//
//                // 서비스 계층을 이용하여 Elasticsearch 인덱싱 요청
//                elasticSearchService.indexProduct(name, sku);
//            } catch (Exception e) {
//                log.error("Error while registering product or indexing to Elasticsearch: ", e);
//                // 예외가 발생해도 트랜잭션이 롤백되지 않도록 적절히 처리할 수 있습니다.
//            }
//        }
//    }
//
//
//    @Test
//    @Rollback(false)
//    public void testRegisterDummiesFromJson() throws Exception {
//
//        // JSON 파일 경로
//        String jsonFilePath = "src/test/resources/product_data.json";
//
//        // JSON 파일 읽기
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Map<String, Object>> productList = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Map<String, Object>>>() {});
//
//        // 데이터 반복 처리
//        for (Map<String, Object> productData : productList) {
//
//            // JSON 데이터에서 값 추출
//            String name = (String) productData.get("name");
//            String sku = (String) productData.get("sku");
//            Integer price = (Integer) productData.get("price");
//            String fileUrl = (String) productData.get("file");  // 로컬 경로
//            Long categoryID = ((Number) productData.get("categoryID")).longValue();
//            Long martID = ((Number) productData.get("martID")).longValue();
//
//            try {
//                // MockMultipartFile 생성 (fileUrl에서 다운로드)
//                MultipartFile mockFile = createMockMultipartFileFromUrl(fileUrl);
//
//                // ProductAddDTO 생성
//                ProductAddDTO dto = ProductAddDTO.builder()
//                        .name(name)
//                        .sku(sku)
//                        .price(BigDecimal.valueOf(price))
//                        .files(List.of(mockFile))
//                        .categoryID(categoryID)
//                        .build();
//
//                // 상품 등록
//                Long productId = productService.register(martID, dto); // martID 추가
//                assertNotNull(productId, "Product ID should not be null after registration");
//
//                // 서비스 계층을 이용하여 Elasticsearch 인덱싱 요청
//                elasticSearchService.indexProduct(name, sku);
//
//            } catch (FileNotFoundException e) {
//                log.error("File not found: {}", fileUrl, e);
//                continue; // 파일이 없으면 해당 데이터를 건너뛰기
//            } catch (Exception e) {
//                log.error("Error while registering product or indexing to Elasticsearch: ", e);
//                // 다른 예외는 로그만 남기고 계속 진행
//            }
//        }
//    }
//
//    // MockMultipartFile 생성
//    private MockMultipartFile createMockMultipartFileFromUrl(String filePath) throws IOException {
//        // 로컬 파일 객체 생성
//        File file = new File(filePath); // "C:\\path\\to\\file" 형식의 로컬 경로
//        if (!file.exists()) {
//            throw new FileNotFoundException("File not found: " + filePath);
//        }
//
//        // MIME 타입 추론 (파일 확장자 기준)
//        String mimeType = Files.probeContentType(file.toPath());
//        if (mimeType == null) {
//            mimeType = "application/octet-stream"; // 기본 MIME 타입 설정
//        }
//
//        // InputStream으로 파일 읽기
//        try (InputStream inputStream = new FileInputStream(file)) {
//            // MockMultipartFile 생성 (파일 이름 및 MIME 타입 포함)
//            return new MockMultipartFile(
//                    "file",
//                    file.getName(),
//                    mimeType,
//                    inputStream
//            );
//        }
//    }
//
//
//}
