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
import org.sunbong.allmart_api.flyer.domain.ProducedVideo;
import org.sunbong.allmart_api.flyer.dto.*;
import org.sunbong.allmart_api.flyer.repository.FlyerRepository;
import org.sunbong.allmart_api.flyer.repository.ProducedVideoRepository;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.repository.MartRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class FlyerService {

    private final FlyerRepository flyerRepository;
    private final CustomFileUtil fileUtil;
    private final MartRepository martRepository;
    private final ProducedVideoRepository producedVideoRepository;

    // 조회
    public FlyerReadDTO readById(Long id) {

        FlyerReadDTO result = flyerRepository.readById(id);

        return result;
    }

    // flyer 조회
    public Flyer findById(Long id) {

        Optional<Flyer> byId = flyerRepository.findById(id);

        return byId.orElse(null);
    }

    // 리스트
    public PageResponseDTO<FlyerListDTO> list(PageRequestDTO pageRequestDTO) {

        PageResponseDTO<FlyerListDTO> result = flyerRepository.list(pageRequestDTO);

        return result;
    }

    // 리스트
    public PageResponseDTO<FlyerSystemManagerListDTO> listSystem(PageRequestDTO pageRequestDTO) {

        PageResponseDTO<FlyerSystemManagerListDTO> result = flyerRepository.listSystem(pageRequestDTO);

        return result;
    }



    // FlyerAddDTO -> Flyer 변환
    private Flyer dtoToEntity(FlyerAddDTO dto) throws IllegalAccessException {

        Mart mart = martRepository.findById(dto.getMartId()).orElseThrow(() ->  new IllegalAccessException("Invalid Mart ID"));
        return Flyer.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .attachImages(dto.getAttachImages())
                .audioURL(dto.getAudioURL())
                .mart(mart)
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
//        fileUtil.saveFiles(dto.getAttachImages());
//        fileUtil.saveFiles(dto.getAudioURL());
        Flyer flyer = dtoToEntity(dto);
        flyerRepository.save(flyer);

        return flyer.getFlyerID();
    }


    public void sendFlyerData(ProducedVideoListDTO dto) throws Exception {
        log.info("Processing Flyer Data for Sending: {}", dto);

        // Flyer를 기반으로 ProducedVideo 생성
        Flyer flyer = flyerRepository.findById(dto.getFlyerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Flyer ID"));

        ProducedVideo video = ProducedVideo.builder()
                .fileName(dto.getFileName())
                .size("10MB") // 예제 데이터
                .uploadDate(LocalDate.now())
                .link("http://example.com/link") // 예제 데이터
                .originalFile("http://example.com/original") // 예제 데이터
                .memo("자동 생성된 전송 데이터")
                .flyer(flyer)
                .build();

        producedVideoRepository.save(video);

        log.info("ProducedVideo saved: {}", video);
    }







}
