//package org.sunbong.allmart_api.product.repository;
//
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Commit;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//import org.sunbong.allmart_api.common.dto.PageRequestDTO;
//import org.sunbong.allmart_api.common.dto.PageResponseDTO;
//import org.sunbong.allmart_api.product.dto.ProductListDTO;
//import org.sunbong.allmart_api.product.dto.ProductReadDTO;
//
//import static org.hibernate.validator.internal.util.Contracts.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@Log4j2
//@SpringBootTest
//@Transactional
//@Commit
//public class ProductRepositoryTest {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Test
//    public void testRead() {
//
//        long productID = 1L;
//
//        ProductReadDTO productReadDTO = productRepository.readById(productID);
//
//        log.info("ProductReadDTO: " + productReadDTO);
//
//        // 검증
//        assertNotNull(productReadDTO, "ProductReadDTO should not be null");
//        assertEquals(1L, productReadDTO.getProductID(), "Product ID should match");
//        assertNotNull(productReadDTO.getName(), "Product name should not be null");
//        assertNotNull(productReadDTO.getPrice(), "Product price should not be null");
//        assertTrue(productReadDTO.getAttachImages().size() > 0, "Attach files should be present");
//    }
//
//    @Test
//    public void testList() {
//
//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
//
//        PageResponseDTO<ProductListDTO> result = productRepository.list(pageRequestDTO);
//
//        // DTO 리스트 출력
//        result.getDtoList().forEach(dto -> {
//            log.info("Product DTO: " + dto);
//        });
//
//        // 검증
//        assertNotNull(result, "Result should not be null");
//    }
//
//    @Test
//    public void testSearch() {
//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
//                .page(1)
//                .size(10)
//                .keyword("피카츄")
//                .type("name&sku")
//                .categoryID(1L)  // 검색할 카테고리 ID 추가
//                .build();
//
//        PageResponseDTO<ProductListDTO> result = productRepository.list(pageRequestDTO);
//
//        result.getDtoList().forEach(dto -> log.info("Search Result DTO: " + dto));
//
//        // 검증
//        assertNotNull(result, "Search result should not be null");
//    }
//
//    @Test
//    public void testDelete() {
//
//        Long productID = 101L;
//
//        // Product 삭제
//        productRepository.deleteById(productID);
//
//        boolean exists = productRepository.findById(productID).isPresent();
//
//        assertTrue(!exists, "Product should be deleted");
//    }
//
//}
