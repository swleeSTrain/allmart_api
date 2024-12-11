package org.sunbong.allmart_api.flyer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.flyer.domain.Flyer;
import org.sunbong.allmart_api.flyer.dto.*;
import org.sunbong.allmart_api.flyer.repository.FlyerRepository;
import org.sunbong.allmart_api.flyer.service.FlyerService;

@RestController
@RequestMapping("/api/v1/flyer")
@Log4j2
@RequiredArgsConstructor
public class FlyerController {

    private final FlyerService flyerService;
    private final FlyerRepository flyerRepository;

    // 조회
    @GetMapping("/{flyerID}")
    public ResponseEntity<FlyerReadDTO> readById(
            @PathVariable("flyerID") Long flyerID
    ) {
        log.info("=======Flyer ReadById: {} =======", flyerID);

        return ResponseEntity.ok(flyerService.readById(flyerID));
    }

    // 목록
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<FlyerListDTO>> list(
            @Validated PageRequestDTO pageRequestDTO
    ) {
        log.info("=======Flyer List=======");

        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        PageResponseDTO<FlyerListDTO> response = flyerService.list(pageRequestDTO);

        // DTO에 martName 포함
        response.getDtoList().forEach(dto -> {
            Flyer flyer = flyerRepository.findById(dto.getFlyerID()).orElse(null);
            if (flyer != null && flyer.getMart() != null) {
                dto.setMartName(flyer.getMart().getMartName());
            }
        });

        return ResponseEntity.ok(response);
    }




    @GetMapping("/list/system")
    public ResponseEntity<PageResponseDTO<FlyerSystemManagerListDTO>> listFlyerSystem(
            @Validated PageRequestDTO pageRequestDTO)
     {
        log.info("=======Flyer List=======");

        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return ResponseEntity.ok(flyerService.listSystem(pageRequestDTO));
    }

    // 전단지 등록
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody FlyerAddDTO flyerAddDTO) {
        log.info("Registering flyer: {}", flyerAddDTO);
        try {
            Long flyerId = flyerService.register(flyerAddDTO);
               return ResponseEntity.ok(flyerId);
        } catch (Exception e) {
            log.error("Error registering flyer: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendFlyerData(@RequestBody ProducedVideoListDTO dto) {
        log.info("Flyer Data Received for Sending: {}", dto);

        try {
            // 전송 로직 (서비스로 위임)
            flyerService.sendFlyerData(dto);
            return ResponseEntity.ok("Flyer data sent successfully");
        } catch (Exception e) {
            log.error("Error while sending flyer data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send flyer data");
        }
    }

//    // 전단지 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<FlyerReadDTO> readById(@PathVariable Long id) {
//        log.info("Fetching flyer with ID: {}", id);
//        FlyerReadDTO flyerReadDTO = flyerService.readById(id);
//        return ResponseEntity.ok(flyerReadDTO);
//    }


}
