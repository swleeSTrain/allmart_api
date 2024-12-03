//package org.sunbong.allmart_api.flyer.repository;
//
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//import org.sunbong.allmart_api.common.dto.PageRequestDTO;
//import org.sunbong.allmart_api.common.dto.PageResponseDTO;
//import org.sunbong.allmart_api.flyer.domain.Flyer;
//import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
//import org.sunbong.allmart_api.flyer.dto.FlyerReadDTO;
//import org.sunbong.allmart_api.product.dto.ProductReadDTO;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Log4j2
//@Transactional
//@Commit
//class FlyerRepositoryTest {
//
//    @Autowired
//    private FlyerRepository flyerRepository;
//
//    @Test
//    public void testRead() {
//
//        long flyerID = 1L;
//
//        FlyerReadDTO flyerReadDTO = flyerRepository.readById(flyerID);
//
//        log.info("FlyerReadDTO: " + flyerReadDTO);
//
//    }
//
//    @Test
//    public void testList() {
//
//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
//
//        PageResponseDTO<FlyerListDTO> result = flyerRepository.list(pageRequestDTO);
//
//        // DTO 리스트 출력
//        result.getDtoList().forEach(dto -> {
//            log.info("Flyer DTO: " + dto);
//        });
//
//        // 검증
//        assertNotNull(result, "Result should not be null");
//    }
//
//    @Test
//    public void testAdd() {
//        for (int i = 1; i <= 100; i++) {
//
//            // Question 엔티티 생성
//            Flyer flyer = Flyer.builder()
//                    .title("제목 " + i)
//                    .content("내용 " + i)
//                    .build();
//
//            flyer.addAudioURL("audio" + i + ".mp3");
//
//            flyer.addImage("file" + i + "_1.jpg");
//
//            // 저장
//            flyerRepository.save(flyer);
//        }
//    }
//
//}