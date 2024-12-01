package org.sunbong.allmart_api.mart.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.domain.QMart;
import org.sunbong.allmart_api.mart.domain.QMartLogo;
import org.sunbong.allmart_api.mart.dto.MartListDTO;
import org.sunbong.allmart_api.mart.dto.MartReadDTO;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class MartSearchImpl extends QuerydslRepositorySupport implements MartSearch {

    public MartSearchImpl() { super(Mart.class); }

    @Override
    public PageResponseDTO<MartListDTO> list(PageRequestDTO pageRequestDTO) {

        log.info("-------------------list----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("martID").descending()
        );

        QMart mart = QMart.mart;
        QMartLogo attachLogo = QMartLogo.martLogo;

        BooleanBuilder builder = new BooleanBuilder();
        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();

        if (keyword != null && type != null) {
            if (type.contains("martName")) {
                builder.or(mart.martName.containsIgnoreCase(keyword));
            }
            if (type.contains("phoneNumber")) {
                builder.or(mart.phoneNumber.containsIgnoreCase(keyword));
            }
        }

        JPQLQuery<Mart> query = from(mart)
                .join(mart.attachLogo, attachLogo)
                .where(builder)
                .where(attachLogo.ord.eq(0))
                .where(mart.delFlag.eq(false))
                .groupBy(mart);

        // 페이징 적용
        getQuerydsl().applyPagination(pageable, query);

        List<Mart> martList = query.fetch();
        long total = query.fetchCount();

        // DTO 변환
        List<MartListDTO> dtoList = martList.stream()
                .map(mar -> MartListDTO.builder()
                        .martID(mar.getMartID())
                        .martName(mar.getMartName())
                        .phoneNumber(mar.getPhoneNumber())
                        .address(mar.getAddress())
                        .thumbnailImage(mar.getAttachLogo().isEmpty() ? null : mar.getAttachLogo().get(0).getLogoURL())
                        .build()
                ).collect(Collectors.toList());

        return PageResponseDTO.<MartListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public MartReadDTO readById(Long martID) {

        log.info("-------------------read----------");

        QMart mart = QMart.mart;
        QMartLogo logo = QMartLogo.martLogo;

        JPQLQuery<Mart> query = from(mart)
                .leftJoin(mart.attachLogo, logo).fetchJoin()
                .where(mart.martID.eq(martID))
                .where(mart.delFlag.eq(false));

        Mart result = query.fetchOne();

        if (result == null) {
            return null;
        }

        // DTO 변환 (attachLogo의 파일 이름을 문자열 리스트로 변환)
        List<String> attachLogo = result.getAttachLogo().stream()
                .map(file -> file.getLogoURL())
                .collect(Collectors.toList());

        return MartReadDTO.builder()
                .martID(result.getMartID())
                .martName(result.getMartName())
                .phoneNumber(result.getPhoneNumber())
                .address(result.getAddress())
                .certificate(result.getCertificate())
                .attachLogo(attachLogo)
                .createdDate(result.getCreatedDate())
                .modifiedDate(result.getModifiedDate())
                .build();
    }
}
