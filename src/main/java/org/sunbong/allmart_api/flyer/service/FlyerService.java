package org.sunbong.allmart_api.flyer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.util.CustomFileUtil;
import org.sunbong.allmart_api.flyer.domain.Flyer;
import org.sunbong.allmart_api.flyer.domain.FlyerImage;
import org.sunbong.allmart_api.flyer.dto.FlyerAddDTO;
import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
import org.sunbong.allmart_api.flyer.dto.FlyerReadDTO;
import org.sunbong.allmart_api.flyer.repository.FlyerRepository;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class FlyerService {

    private final FlyerRepository flyerRepository;
    private final CustomFileUtil fileUtil;

    // 조회
    public FlyerReadDTO readById(Long id) {

        FlyerReadDTO result = flyerRepository.readById(id);

        return result;
    }

    // 리스트
    public PageResponseDTO<FlyerListDTO> list(PageRequestDTO pageRequestDTO) {

        PageResponseDTO<FlyerListDTO> result = flyerRepository.list(pageRequestDTO);

        return result;
    }

    // FlyerAddDTO -> Flyer 변환
    private Flyer dtoToEntity(FlyerAddDTO dto) {
        return Flyer.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .attachImages(dto.getAttachImages())
                .audioURL(dto.getAudioURL())
                .build();
    }

    // Flyer -> FlyerReadDTO 변환
    private FlyerReadDTO entityToReadDTO(Flyer flyer) {
        return FlyerReadDTO.builder()
                .flyerID(flyer.getFlyerID())
                .title(flyer.getTitle())
                .content(flyer.getContent())
                .attachImages(flyer.getAttachImages().stream().map(FlyerImage::getImageURL).toList())
                .audioURL(flyer.getAudioURL())
                .createdDate(flyer.getCreatedDate())
                .modifiedDate(flyer.getModifiedDate())
                .build();
    }

    // 등록
    public Long register(FlyerAddDTO dto) throws Exception {
        log.info("Registering flyer: {}", dto);

        Flyer flyer = dtoToEntity(dto);
        flyerRepository.save(flyer);

        return flyer.getFlyerID();
    }







}
