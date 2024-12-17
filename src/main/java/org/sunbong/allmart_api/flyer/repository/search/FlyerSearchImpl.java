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
import org.sunbong.allmart_api.flyer.domain.Flyer;
import org.sunbong.allmart_api.flyer.domain.FlyerImage;
import org.sunbong.allmart_api.flyer.domain.QFlyer;
import org.sunbong.allmart_api.flyer.domain.QFlyerImage;
import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
import org.sunbong.allmart_api.flyer.dto.FlyerReadDTO;
import org.sunbong.allmart_api.flyer.dto.FlyerSystemManagerListDTO;
import org.sunbong.allmart_api.flyer.dto.ProducedVideoDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Log4j2
public class FlyerSearchImpl extends QuerydslRepositorySupport implements FlyerSearch {

    public FlyerSearchImpl() {
        super(Flyer.class);
    }

    @Override
    public PageResponseDTO<FlyerListDTO> list(PageRequestDTO pageRequestDTO) {

        log.info("-------------------list----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("flyerID").descending()
        );

        QFlyer flyer = QFlyer.flyer;
        QFlyerImage attachImage = QFlyerImage.flyerImage;

        BooleanBuilder builder = new BooleanBuilder();
        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();

        if (keyword != null && type != null) {
            if (type.contains("title")) {
                builder.or(flyer.title.containsIgnoreCase(keyword));
            }
            if (type.contains("content")) {
                builder.or(flyer.content.containsIgnoreCase(keyword));
            }
        }

        JPQLQuery<Flyer> query = from(flyer)
                .join(flyer.attachImages, attachImage)
                .leftJoin(flyer.audioURL)
                .where(builder)
                .where(attachImage.ord.eq(0))
                .groupBy(flyer);

        // 페이징 적용
        getQuerydsl().applyPagination(pageable, query);

        List<Flyer> flyerList = query.fetch();
        long total = query.fetchCount();

        List<FlyerListDTO> dtoList = flyerList.stream()
                .map(fly -> FlyerListDTO.builder()
                        .flyerID(fly.getFlyerID())
                        .title(fly.getTitle())
                        .content(fly.getContent())
                        .thumbnailImage(fly.getAttachImages().isEmpty() ? null : fly.getAttachImages().get(0).getImageURL())
                        .build()
                ).collect(Collectors.toList());

        return PageResponseDTO.<FlyerListDTO>withAll()
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




    public PageResponseDTO<FlyerSystemManagerListDTO> listSystem(PageRequestDTO pageRequestDTO) {

        log.info("-------------------list----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("flyerID").descending()
        );

        QFlyer flyer = QFlyer.flyer;
        QFlyerImage attachImage = QFlyerImage.flyerImage;

        BooleanBuilder builder = new BooleanBuilder();
        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();

        if (keyword != null && type != null) {
            if (type.contains("title")) {
                builder.or(flyer.title.containsIgnoreCase(keyword));
            }
            if (type.contains("content")) {
                builder.or(flyer.content.containsIgnoreCase(keyword));
            }
        }

        JPQLQuery<Flyer> query = from(flyer)
                .join(flyer.attachImages, attachImage)
                .leftJoin(flyer.audioURL)
                .where(builder)
                .where(attachImage.ord.eq(0))
                .groupBy(flyer);

        // 페이징 적용
        getQuerydsl().applyPagination(pageable, query);

        List<Flyer> flyerList = query.fetch();
        long total = query.fetchCount();

        List<FlyerSystemManagerListDTO> dtoList = flyerList.stream()
                .map(fly -> FlyerSystemManagerListDTO.builder()
                        .flyerID(fly.getFlyerID())
                        .title(fly.getTitle())
                        .content(fly.getContent())
                        .martName(fly.getMart().getMartName())
                        .attachImages(fly.getAttachImages())
                        .audioURL(fly.getAudioURL())
                        .createdDate(fly.getCreatedDate())
                        .thumbnailImage(fly.getAttachImages().isEmpty() ? null : fly.getAttachImages().get(0).getImageURL())
                        .producedVideo(fly.getProducedVideo() != null
                                        ? new ProducedVideoDTO(
                                                fly.getProducedVideo().getFileName(),
                                                fly.getProducedVideo().getLink(),
                                                fly.getProducedVideo().getOriginalFile()
                                ):null)
                        .build())
                .collect(Collectors.toList());

        return PageResponseDTO.<FlyerSystemManagerListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

}
