package org.sunbong.allmart_api.flyer.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.flyer.domain.*;
import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
import org.sunbong.allmart_api.flyer.dto.FlyerReadDTO;
import org.sunbong.allmart_api.flyer.dto.ProducedVideoListDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class ProducedVideoSearchImpl extends QuerydslRepositorySupport implements ProducedVideoSearch {

    public ProducedVideoSearchImpl() {
        super(ProducedVideo.class);
    }

    @Override
    public PageResponseDTO<ProducedVideoListDTO> list(PageRequestDTO pageRequestDTO) {

        log.info("-------------------list----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("videoID").descending()
        );

        QProducedVideo producedVideo = QProducedVideo.producedVideo;
        QFlyerImage attachImage = QFlyerImage.flyerImage;


        BooleanBuilder builder = new BooleanBuilder();
        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();

        if (keyword != null && type != null) {
            if (type.contains("fileName")) {
                builder.or(producedVideo.fileName.containsIgnoreCase(keyword));
            }
            if (type.contains("memo")) {
                builder.or(producedVideo.memo.containsIgnoreCase(keyword));
            }
        }


        JPQLQuery<ProducedVideo> query = from(producedVideo)
                .join(producedVideo.flyer)
                .where(builder)
                .groupBy(producedVideo);


        // 페이징 적용
        getQuerydsl().applyPagination(pageable, query);

        List<ProducedVideo> producedVideoList = query.fetch();
        long total = query.fetchCount();

        List<ProducedVideoListDTO> dtoList = producedVideoList.stream()
                .map(producedVideo1 -> ProducedVideoListDTO.builder()
                        .fileName(producedVideo1.getFileName())
                        .memo(producedVideo1.getMemo())
                        .link(producedVideo1.getLink())
                        .originalFile(producedVideo1.getOriginalFile())
                        .size(producedVideo1.getSize())
                        .uploadDate(producedVideo1.getUploadDate())
                        .build()
                ).collect(Collectors.toList());

        return PageResponseDTO.<ProducedVideoListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public FlyerReadDTO readById(Long flyerID) {

        log.info("-------------------read----------");

        QFlyer flyer = QFlyer.flyer;
        QFlyerImage attachImage = QFlyerImage.flyerImage;

        JPQLQuery<Flyer> query = from(flyer)
                .leftJoin(flyer.attachImages, attachImage).fetchJoin() // attachImages fetch
                .where(flyer.flyerID.eq(flyerID));

        Flyer result = query.fetchOne();

        if (result == null) {
            return null;
        }

        // attachImages의 파일 이름 리스트 생성
        List<String> attachImages = result.getAttachImages().stream()
                .map(file -> file.getImageURL())
                .collect(Collectors.toList());

        // audioURL Set 가져오기
        Set<String> audioURLs = result.getAudioURL();

        return FlyerReadDTO.builder()
                .flyerID(result.getFlyerID())
                .title(result.getTitle())
                .content(result.getContent())
                .attachImages(attachImages)
                .audioURL(audioURLs) // audioURL 추가
                .createdDate(result.getCreatedDate())
                .modifiedDate(result.getModifiedDate())
                .build();
    }

}
