package org.sunbong.allmart_api.mart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.util.CustomFileUtil;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.dto.MartAddDTO;
import org.sunbong.allmart_api.mart.dto.MartListDTO;
import org.sunbong.allmart_api.mart.repository.MartRepository;

import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class MartService {

    private final MartRepository martRepository;
    private final CustomFileUtil fileUtil;

    // 리스트
    public PageResponseDTO<MartListDTO> list(PageRequestDTO pageRequestDTO) {

        PageResponseDTO<MartListDTO> result = martRepository.list(pageRequestDTO);

        return result;
    }

    // 등록
    public Long register(MartAddDTO dto) throws Exception {

        Mart mart = Mart.builder()
                .martName(dto.getMartName())
                .phoneNumber(dto.getPhoneNumber())
                .template(dto.getTemplate())
                .address(dto.getAddress())
                .certificate(dto.getCertificate())
                .build();

        // 업로드할 파일이 있을 경우
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<String> savedFileNames = fileUtil.saveFiles(dto.getFiles());
            savedFileNames.forEach(mart::addLogo);
        }

        Mart savedMart = martRepository.save(mart);

        return savedMart.getMartID();
    }
}
