package org.sunbong.allmart_api.product.dummy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.sunbong.allmart_api.category.domain.Category;
import org.sunbong.allmart_api.category.repository.CategoryRepository;
import org.sunbong.allmart_api.common.util.CustomFileUtil;
import org.sunbong.allmart_api.product.dto.ProductAddDTO;
import org.sunbong.allmart_api.product.service.ProductService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@Commit
@Log4j2
public class ProductDummy {

    @Autowired
    private ProductService productService;

    @Autowired
    private CustomFileUtil fileUtil;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testAddCategoriesInOrder() {
        // 카테고리 이름 리스트 (순서대로)
        List<String> categoryNames = List.of(
                "과일", "채소", "쌀/잡곡/견과", "정육/계란류", "수산물/건해산",
                "우유/유제품", "밀키트/간편식", "김치/반찬/델리", "생수/음료/주류",
                "커피/원두/차", "면류/통조림", "양념/오일", "과자/간식", "베이커리/잼",
                "건강식품", "친환경/유기농", "헤어/바디/뷰티", "청소/생활용품",
                "주방용품", "생활잡화/공구", "반려동물"
        );

        // 카테고리 저장
        for (String categoryName : categoryNames) {
            if (!categoryRepository.findByName(categoryName).isPresent()) {
                Category category = Category.builder()
                        .name(categoryName)
                        .build();

                categoryRepository.save(category);
                log.info(categoryName + " 카테고리가 생성되었습니다.");
            } else {
                log.info(categoryName + "는 이미 존재하는 카테고리입니다.");
            }
        }

        // 검증
        assertThat(categoryRepository.findAll().size()).isGreaterThanOrEqualTo(categoryNames.size());
    }

    @Test
    @Rollback(false)
    public void testRegisterDummiesFromJson() throws Exception {
        // JSON 파일 경로
        String jsonFilePath = "src/test/resources/product_data.json";

        // JSON 파일 읽기
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> productList = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Map<String, Object>>>() {});

        // 데이터 반복 처리
        for (Map<String, Object> productData : productList) {
            // JSON 데이터에서 값 추출
            String name = (String) productData.get("name");
            String sku = (String) productData.get("sku");
            Integer price = (Integer) productData.get("price");
            String fileUrl = (String) productData.get("file");
            Long categoryID = ((Number) productData.get("categoryID")).longValue();

            // MockMultipartFile 생성 (fileUrl에서 다운로드)
            MultipartFile mockFile = createMockMultipartFileFromUrl(fileUrl);

            // ProductAddDTO 생성
            ProductAddDTO dto = ProductAddDTO.builder()
                    .name(name)
                    .sku(sku)
                    .price(BigDecimal.valueOf(price))
                    .files(List.of(mockFile))
                    .categoryID(categoryID)
                    .build();

            // 상품 등록
            Long productId = productService.register(dto);
            assertNotNull(productId, "Product ID should not be null after registration");
        }
    }

    // URL로부터 MockMultipartFile 생성
    private MockMultipartFile createMockMultipartFileFromUrl(String fileUrl) throws IOException {
        // URL로부터 파일 데이터 읽기
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download file from URL: " + fileUrl);
        }

        try (InputStream inputStream = connection.getInputStream()) {
            // 파일 이름 추출
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            return new MockMultipartFile(
                    "file",
                    filename,
                    connection.getContentType(),
                    inputStream
            );
        }
    }
}
