package org.sunbong.allmart_api.flyer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.flyer.domain.Flyer;
import org.sunbong.allmart_api.flyer.domain.ProducedVideo;
import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
import org.sunbong.allmart_api.flyer.dto.ProducedVideoListDTO;
import org.sunbong.allmart_api.flyer.repository.search.ProducedVideoSearch;
import org.sunbong.allmart_api.flyer.service.FileService;
import org.sunbong.allmart_api.flyer.service.FlyerService;
import org.sunbong.allmart_api.flyer.service.ProducedVideoService;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class ProducedVideoController {

    private final ProducedVideoService producedVideoService;
    private final FileService fileService;
    private final FlyerService flyerService;



//    @PostMapping("/save")
//    public ResponseEntity<ProducedVideo> createVideo(@RequestBody ProducedVideo video) {
//        return ResponseEntity.ok(producedVideoService.save(video));
//    }

    @GetMapping("/view/{id}")
    public ResponseEntity<ProducedVideo> getVideo(@PathVariable Long id) {
        return ResponseEntity.ok(producedVideoService.findById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ProducedVideo>> getAllVideos() {
        return ResponseEntity.ok(producedVideoService.getAllVideos());
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<ProducedVideoListDTO>> list(
            @Validated PageRequestDTO pageRequestDTO
    ) {
        log.info("=======ProducucedVideo List=======");

        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return ResponseEntity.ok(producedVideoService.list(pageRequestDTO));
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveProducedVideo(
            @RequestParam("martName") String martName,
            @RequestParam("flyerTitle") String flyerTitle,
            @RequestParam("fileName") String fileName,
            @RequestParam("link") String link,
            @RequestParam("file") MultipartFile file,
            @RequestParam("flyerId") Long flyerId // optional 처리
    ) {
        if (flyerId == null) {
            return ResponseEntity.badRequest().body("flyerId가 누락되었습니다.");
        }

        try {
            // 파일 저장 로직
            String uploadedFilePath = fileService.saveFile(file);

            // Flyer ID 검증
            Flyer flyer = flyerService.findById(flyerId);
            if (flyer == null) {
                return ResponseEntity.badRequest().body("유효하지 않은 flyerId입니다.");
            }

            // 데이터 저장
            ProducedVideo video = ProducedVideo.builder()
                    .fileName(fileName)
                    .size(file.getSize() + " bytes")
                    .uploadDate(LocalDate.now())
                    .link(link)
                    .originalFile(uploadedFilePath)
                    .flyer(flyer)
                    .memo("자동 저장")
                    .build();

            producedVideoService.save(video);

            return ResponseEntity.ok("저장 성공");
        } catch (Exception e) {
            log.error("저장 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장 실패");
        }
    }



}
