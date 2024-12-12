package org.sunbong.allmart_api.flyer.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.util.CustomFileUtil;
import org.sunbong.allmart_api.flyer.domain.ProducedVideo;
import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
import org.sunbong.allmart_api.flyer.dto.ProducedVideoListDTO;
import org.sunbong.allmart_api.flyer.repository.ProducedVideoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducedVideoService {

    private final CustomFileUtil customFileUtil;

    private final ProducedVideoRepository producedVideoRepository;

    public ProducedVideo save(ProducedVideo video) {

        return producedVideoRepository.save(video);
    }

    public ProducedVideo findById(Long id) {
        return producedVideoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found: " + id));
    }

    public List<ProducedVideo> getAllVideos() {
        List<ProducedVideo> allProducedVideo = producedVideoRepository.findAll();
        return allProducedVideo;
    }

    //목록
    public PageResponseDTO<ProducedVideoListDTO> list(PageRequestDTO pageRequestDTO) {

        PageResponseDTO<ProducedVideoListDTO> result = producedVideoRepository.list(pageRequestDTO);

        return result;
    }
}