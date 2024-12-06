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
import org.sunbong.allmart_api.mart.dto.MartEditDTO;
import org.sunbong.allmart_api.mart.dto.MartListDTO;
import org.sunbong.allmart_api.mart.dto.MartReadDTO;
import org.sunbong.allmart_api.mart.repository.MartRepository;

import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class MartService {

    private final MartRepository martRepository;
    private final CustomFileUtil fileUtil;

    // 조회
    public MartReadDTO readById(Long id) {

        MartReadDTO result = martRepository.readById(id);

        return result;
    }

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
                .lat(dto.getLat())
                .lng(dto.getLng())
                .build();

        // 업로드할 파일이 있을 경우
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<String> savedFileNames = fileUtil.saveFiles(dto.getFiles());
            savedFileNames.forEach(mart::addLogo);
        }

        Mart savedMart = martRepository.save(mart);

        return savedMart.getMartID();
    }

    // 소프트 삭제
    public Long delete(Long id) {

        Mart mart = martRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        // Product 소프트 딜리트 처리
        mart.softDelete();
        martRepository.save(mart);

        return mart.getMartID();
    }

    // 수정
    public Long edit(Long id, MartEditDTO dto) throws Exception {

        Mart existingMart = martRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mart ID" ));

        List<String> retainedFiles = dto.getExistingFileNames();

        if (retainedFiles != null) {
            // 기존 파일 중 삭제된 파일 처리 (retainedFiles에 포함되지 않은 파일 삭제)
            existingMart.getAttachLogo()
                    .removeIf(file -> !retainedFiles.contains(file.getLogoURL()));
        } else {
            // retainedFiles가 null이면 모든 파일을 삭제
            existingMart.clearLogo();
        }

        // 새 파일 저장
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            // 파일 저장 로직 확인: 파일이 제대로 저장되고 경로가 반환되는지 확인
            List<String> newFileNames = fileUtil.saveFiles(dto.getFiles());

            // 새로 저장된 파일들 addFile을 통해 기존 Product에 추가
            newFileNames.forEach(existingMart::addLogo);
        }

        Mart updatedMart = existingMart.toBuilder()
                .martName(dto.getMartName() != null ? dto.getMartName() : existingMart.getMartName())
                .phoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : existingMart.getPhoneNumber())
                .template(dto.getTemplate() != null ? dto.getTemplate() : existingMart.getTemplate())
                .address(dto.getAddress() != null ? dto.getAddress() : existingMart.getAddress())
                .certificate(dto.getCertificate() != null ? dto.getCertificate() : existingMart.getCertificate())
                .build();

        martRepository.save(updatedMart);

        return updatedMart.getMartID();
    }


}
