package org.sunbong.allmart_api.flyer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.flyer.domain.ProducedVideo;
import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
import org.sunbong.allmart_api.flyer.dto.ProducedVideoListDTO;
import org.sunbong.allmart_api.flyer.repository.search.ProducedVideoSearch;
import org.sunbong.allmart_api.flyer.service.ProducedVideoService;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class ProducedVideoController {

    private final ProducedVideoService producedVideoService;

    @PostMapping("/save")
    public ResponseEntity<ProducedVideo> createVideo(@RequestBody ProducedVideo video) {
        return ResponseEntity.ok(producedVideoService.save(video));
    }

    @GetMapping("/{id}")
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
}
