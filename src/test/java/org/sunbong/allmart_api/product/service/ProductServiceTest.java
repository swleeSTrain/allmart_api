//package org.sunbong.allmart_api.product.service;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import org.sunbong.allmart_api.category.domain.Category;
//import org.sunbong.allmart_api.category.repository.CategoryRepository;
//import org.sunbong.allmart_api.common.util.CustomFileUtil;
//import org.sunbong.allmart_api.product.domain.Product;
//import org.sunbong.allmart_api.product.dto.ProductAddDTO;
//import org.sunbong.allmart_api.product.dto.ProductEditDTO;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@Transactional
//@Commit
//@Log4j2
//public class ProductServiceTest {
//
//    @Autowired
//    private ProductService productService;
//
//    @Autowired
//    private CustomFileUtil fileUtil;
//
//    @Test
//    public void testRegisterDummiesFromJson() throws Exception {
//        // JSON 파일 경로
//        String jsonFilePath = "C:\\Users\\woals\\Desktop\\product_data.json";
//
//        // JSON 파일 읽기
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Map<String, Object>> productList = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Map<String, Object>>>() {});
//
//        // 데이터 반복 처리
//        for (Map<String, Object> productData : productList) {
//            // JSON 데이터에서 값 추출
//            String name = (String) productData.get("name");
//            String sku = (String) productData.get("sku");
//            Integer price = (Integer) productData.get("price");
//            String fileUrl = (String) productData.get("file");
//            Long categoryID = ((Number) productData.get("categoryID")).longValue();
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
//            Long productId = productService.register(dto);
//            assertNotNull(productId, "Product ID should not be null after registration");
//        }
//    }
//
//    // URL로부터 MockMultipartFile 생성
//    private MockMultipartFile createMockMultipartFileFromUrl(String fileUrl) throws IOException {
//        // URL로부터 파일 데이터 읽기
//        URL url = new URL(fileUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//        connection.connect();
//
//        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//            throw new IOException("Failed to download file from URL: " + fileUrl);
//        }
//
//        try (InputStream inputStream = connection.getInputStream()) {
//            // 파일 이름 추출
//            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//            return new MockMultipartFile(
//                    "file",
//                    filename,
//                    connection.getContentType(),
//                    inputStream
//            );
//        }
//    }
//
//
//
////    @Test
////    public void testRegisterDummies() throws Exception {
////        for (int i = 1; i <= 100; i++) {
////            // 테스트용 더미 파일 생성
////            MultipartFile mockFile = createMockMultipartFile("sample.jpg");
////
////            // ProductAddDTO 생성 (더미 데이터)
////            ProductAddDTO dto = ProductAddDTO.builder()
////                    .name("테스트 상품 " + i)
////                    .sku("TEST-SKU-" + i)
////                    .price(BigDecimal.valueOf(2000 + i)) // 가격은 2000부터 시작해서 i만큼 증가
////                    .files(List.of(mockFile)) // 더미 파일 추가
////                    .categoryID(1L) // 카테고리 ID 설정 (예: 1L)
////                    .build();
////
////            // 상품 등록
////            Long productId = productService.register(dto);
////            assertNotNull(productId, "Product ID should not be null after registration");
////
////        }
////    }
////
////    // 등록 테스트
////    @Test
////    public void testRegister() throws Exception {
////        // 테스트용 파일 생성 및 등록
////        MultipartFile mockFile = createMockMultipartFile("sample.jpg");
////        ProductAddDTO dto = ProductAddDTO.builder()
////                .name("테스트 상품")
////                .sku("TEST-SKU")
////                .price(BigDecimal.valueOf(2000))
////                .files(List.of(mockFile))
////                .build();
////
////        Long productId = productService.register(dto);
////        assertNotNull(productId, "Product ID should not be null after registration");
////    }
////
////    // 수정 테스트
////    @Test
////    public void testEdit() throws Exception {
////
////        Long productId = 101L;
////
////        // 새로운 파일을 추가하고 ord 기준으로 파일 삭제
////        MultipartFile newMockFile = createMockMultipartFile("change_sample.jpg");
////        ProductEditDTO editDto = ProductEditDTO.builder()
////                .name("수정된 상품")
////                .sku("UPDATED-SKU")
////                .price(BigDecimal.valueOf(3500))
////                .files(List.of(newMockFile)) // 새 파일 추가
////                .categoryID(2L) // 변경할 카테고리 ID 전달
////                .build();
////
////        // 제품 수정
////        Long editedProductId = productService.edit(productId, editDto);
////
////        // 기존 ID와 동일한지 확인
////        assertEquals(productId, editedProductId, "Product ID should remain the same after edit");
////
////    }
//
//
//}
//
