package org.sunbong.allmart_api.product.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.product.domain.Product;
import org.sunbong.allmart_api.product.domain.QProduct;
import org.sunbong.allmart_api.product.domain.QProductImage;
import org.sunbong.allmart_api.product.dto.ProductListDTO;
import org.sunbong.allmart_api.product.dto.ProductReadDTO;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ProductSearchImpl extends QuerydslRepositorySupport implements ProductSearch {

    public ProductSearchImpl() {
        super(Product.class);
    }

    @Override
    public PageResponseDTO<ProductListDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("-------------------list----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("productID").descending()
        );

        QProduct product = QProduct.product;
        QProductImage attachFile = QProductImage.productImage;

        BooleanBuilder builder = new BooleanBuilder();
        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();
        Long categoryID = pageRequestDTO.getCategoryID(); // 카테고리 ID 필터

        if (keyword != null && type != null) {
            if (type.contains("name")) {
                builder.or(product.name.containsIgnoreCase(keyword));
            }
            if (type.contains("sku")) {
                builder.or(product.sku.containsIgnoreCase(keyword));
            }
        }

        // 카테고리 ID 필터 추가
        if (categoryID != null) {
            builder.and(product.category.categoryID.eq(categoryID));
        }

        // 엔티티 조회
        JPQLQuery<Product> query = from(product)
                .join(product.attachImages, attachFile) // 필수 항목이면 join
                .where(builder)
                .where(attachFile.ord.eq(0)) // 첫 번째 첨부파일만 가져오기
                .where(product.delFlag.eq(false))
                .groupBy(product);

        // 페이징 적용
        getQuerydsl().applyPagination(pageable, query);

        List<Product> productList = query.fetch();
        long total = query.fetchCount();

        // DTO 변환
        List<ProductListDTO> dtoList = productList.stream()
                .map(prod -> ProductListDTO.builder()
                        .productID(prod.getProductID())
                        .name(prod.getName())
                        .sku(prod.getSku())
                        .price(prod.getPrice())
                        .thumbnailImage(prod.getAttachImages().isEmpty() ? null : prod.getAttachImages().get(0).getImageURL())
                        .build()
                ).collect(Collectors.toList());

        return PageResponseDTO.<ProductListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public ProductReadDTO readById(Long productID) {

        log.info("-------------------read----------");

        QProduct product = QProduct.product;
        QProductImage attachFile = QProductImage.productImage;

        JPQLQuery<Product> query = from(product)
                .leftJoin(product.attachImages, attachFile).fetchJoin()
                .where(product.productID.eq(productID))
                .where(product.delFlag.eq(false));

        Product result = query.fetchOne();

        if (result == null) {
            return null;
        }

        // DTO 변환 (attachImages의 파일 이름을 문자열 리스트로 변환)
        List<String> attachImages = result.getAttachImages().stream()
                .map(file -> file.getImageURL())
                .collect(Collectors.toList());

        return ProductReadDTO.builder()
                .productID(result.getProductID())
                .name(result.getName())
                .sku(result.getSku())
                .price(result.getPrice())
                .categoryID(result.getCategory().getCategoryID())
                .attachImages(attachImages)
                .createdDate(result.getCreatedDate())
                .modifiedDate(result.getModifiedDate())
                .build();
    }

}
