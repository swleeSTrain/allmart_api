package org.sunbong.allmart_api.banner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.banner.domain.Banner;
import org.sunbong.allmart_api.banner.dto.BannerCreateUpdateDTO;
import org.sunbong.allmart_api.banner.dto.BannerReadDTO;
import org.sunbong.allmart_api.banner.repository.BannerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BannerService {

    private final BannerRepository bannerRepository;

    public Long createBanner(BannerCreateUpdateDTO dto) {
        Banner banner = Banner.builder()
                .title(dto.getTitle())
                .link(dto.getLink())
                .content(dto.getContent())
                .image(dto.getImage())
                .martId(dto.getMartId())
                .build();

        bannerRepository.save(banner);
        return banner.getId();
    }

    public void updateBanner(Long id, BannerCreateUpdateDTO dto) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid banner ID: " + id));

        banner.setTitle(dto.getTitle());
        banner.setLink(dto.getLink());
        banner.setContent(dto.getContent());
        banner.setImage(dto.getImage());

        bannerRepository.save(banner);
    }

    public void deleteBanner(Long id) {
        if (!bannerRepository.existsById(id)) {
            throw new IllegalArgumentException("Invalid banner ID: " + id);
        }
        bannerRepository.deleteById(id);
    }

    public BannerReadDTO getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid banner ID: " + id));

        return BannerReadDTO.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .link(banner.getLink())
                .content(banner.getContent())
                .image(banner.getImage())
                .build();
    }

    public List<BannerReadDTO> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(banner -> BannerReadDTO.builder()
                        .id(banner.getId())
                        .title(banner.getTitle())
                        .link(banner.getLink())
                        .content(banner.getContent())
                        .image(banner.getImage())
                        .martId(banner.getMartId())
                        .build())
                .collect(Collectors.toList());
    }
}
