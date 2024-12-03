//package org.sunbong.allmart_api.inventory.repository;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.sunbong.allmart_api.inventory.domain.Inventory;
//
//import java.util.stream.IntStream;
//
//@SpringBootTest
////@Transactional // 테스트 후 데이터 롤백을 위해 사용
//class InventoryRepositoryTest {
//
//    @Autowired
//    private InventoryRepository inventoryRepository;
//
//    @Test
//    void insertBulkData() {
//        // 1부터 100까지의 더미 데이터를 삽입
////        IntStream.rangeClosed(1, 100).forEach(i -> {
////            Inventory inventory = Inventory.builder()
////                    .sku("TESTSKU" + i) // SKU 값을 TESTSKU1, TESTSKU2, ...로 설정
////                    .quantity(i * 10)  // 수량을 i * 10으로 설정
////                    .inStock(1)        // 항상 재고 있음으로 설정
////                    .build();
////
////            inventoryRepository.save(inventory); // 데이터베이스에 저장
////        });
////
////        // 데이터 저장 후 확인
////        long count = inventoryRepository.count(); // 총 데이터 개수 확인
////        System.out.println("Total Inventory Count: " + count);
////
////        // 일부 데이터를 확인
////        inventoryRepository.findAll().stream()
////                .limit(5) // 5개 데이터만 확인
////                .forEach(System.out::println);
//    }
//
//    @Test
//    void insertBulk() {
////        Inventory inventory = Inventory.builder()
////                .sku("TESTSKU1")  // SKU 값을 TESTSKU1로 설정
////                .quantity(10)     // 수량을 10으로 설정
////                .inStock(1)       // 항상 재고 있음으로 설정
////                .build();
////
////        inventoryRepository.save(inventory); // 데이터베이스에 저장
////
////        long count = inventoryRepository.count(); // 총 데이터 개수 확인
////        System.out.println("Total Inventory Count: " + count);
////
////        inventoryRepository.findAll().forEach(System.out::println);
//    }
//}
