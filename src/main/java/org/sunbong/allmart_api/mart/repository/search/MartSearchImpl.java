package org.sunbong.allmart_api.mart.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
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
    public PageResponseDTO<MartListDTO> listWithinRadius(PageRequestDTO pageRequestDTO, double lat, double lng, double radiusKm) {

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
            if (type.contains("martID")) {
                Long id = Long.parseLong(keyword); // keyword를 숫자로 변환
                builder.or(mart.martID.eq(id)); // 숫자 필드에 대한 정확한 값 비교
            }
        }

        // Query 생성 및 거리 조건 추가
        JPQLQuery<Mart> query = from(mart)
                .join(mart.attachLogo, attachLogo)
                .where(builder)
                .where(attachLogo.ord.eq(0))
                .where(mart.delFlag.eq(false)) // 삭제되지 않은 마트만 조회
                .where(calculateDistanceQuery(mart.lat, mart.lng, lat, lng).loe(radiusKm)) // 반경이내에 있는 마트만
                .groupBy(mart);

        getQuerydsl().applyPagination(pageable, query);

        List<Mart> martList = query.fetch();
        long total = query.fetchCount();

        // DTO로 변환
        List<MartListDTO> dtoList = martList.stream()
                .map(m -> MartListDTO.builder()
                        .martID(m.getMartID())
                        .martName(m.getMartName())
                        .phoneNumber(m.getPhoneNumber())
                        .address(m.getAddress())
                        .thumbnailImage(m.getAttachLogo().isEmpty() ? null : m.getAttachLogo().get(0).getLogoURL())
                        .build()
                ).collect(Collectors.toList());

        return PageResponseDTO.<MartListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }


    // Haversine 공식 기반 거리 계산을 위한 QueryDSL 표현식
    private NumberExpression<Double> calculateDistanceQuery(NumberPath<Double> lat1, NumberPath<Double> lng1, double lat, double lng) {
        final double EARTH_RADIUS = 6371; // 지구 반경 (km)
        return Expressions.numberTemplate(Double.class,
                "{0} * acos(cos(radians({1})) * cos(radians({2})) * cos(radians({3}) - radians({4})) + sin(radians({1})) * sin(radians({2})))",
                EARTH_RADIUS, lat, lat1, lng, lng1);
    }


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
            if (type.contains("martID")) {
                Long id = Long.parseLong(keyword); // keyword를 숫자로 변환
                builder.or(mart.martID.eq(id)); // 숫자 필드에 대한 정확한 값 비교
            }
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
                .template(result.getTemplate())
                .address(result.getAddress())
                .certificate(result.getCertificate())
                .attachLogo(attachLogo)
                .createdDate(result.getCreatedDate())
                .modifiedDate(result.getModifiedDate())
                .build();
    }
}
