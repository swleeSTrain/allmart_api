package org.sunbong.allmart_api.banner.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.sunbong.allmart_api.banner.domain.Banner;
import org.sunbong.allmart_api.banner.dto.BannerCreateUpdateDTO;
import org.sunbong.allmart_api.banner.dto.BannerReadDTO;
import org.sunbong.allmart_api.banner.service.BannerService;
import org.sunbong.allmart_api.flyer.service.FileService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Slf4j
public class BannerController {

    private final BannerService bannerService;
    private final FileService fileService;

    @PostMapping("/create")
    public ResponseEntity<Long> createBanner(
            @RequestParam("title") String title,
            @RequestParam("link") String link,
            @RequestParam("content") String content,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("martId") Long martId
    ) {
        log.info("Creating banner with title: {}", title);

        try {
            // 이미지 파일 저장 처리
            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                // 저장 로직 (예: 로컬 디렉토리 또는 S3)
                imagePath = fileService.saveFile(image); // 파일 저장 서비스 활용
            }

            BannerCreateUpdateDTO bannerDTO = new BannerCreateUpdateDTO();
            bannerDTO.setTitle(title);
            bannerDTO.setLink(link);
            bannerDTO.setContent(content);
            bannerDTO.setImage(imagePath); // 저장된 경로 설정
            bannerDTO.setMartId(martId);

            Long bannerId = bannerService.createBanner(bannerDTO); // 배너 생성 서비스 호출
            return ResponseEntity.ok(bannerId);
        } catch (Exception e) {
            log.error("Error creating banner: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateBanner(@PathVariable Long id, @RequestBody BannerCreateUpdateDTO dto) {
        log.info("Updating banner with ID {}: {}", id, dto);
        bannerService.updateBanner(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        log.info("Deleting banner with ID {}", id);
        bannerService.deleteBanner(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BannerReadDTO> getBannerById(@PathVariable Long id) {
        log.info("Fetching banner with ID {}", id);
        BannerReadDTO banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(banner);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<BannerReadDTO>> getAllBanners() {
        log.info("Fetching all banners");
        List<BannerReadDTO> banners = bannerService.getAllBanners();
        log.info("!!!!!banners : ", banners.toString());
        return ResponseEntity.ok(banners);
    }
}
