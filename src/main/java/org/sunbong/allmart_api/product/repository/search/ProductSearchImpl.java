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
import org.sunbong.allmart_api.mart.domain.MartProduct;
import org.sunbong.allmart_api.mart.domain.QMartProduct;
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
    public PageResponseDTO<ProductListDTO> list(Long martID, PageRequestDTO pageRequestDTO) {
        log.info("-------------------list----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("productID").descending()
        );

        QProduct product = QProduct.product;
        QProductImage attachFile = QProductImage.productImage;
        QMartProduct martProduct = QMartProduct.martProduct;

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
        JPQLQuery<MartProduct> query = from(martProduct)
                .join(martProduct.product, product)
                .join(product.attachImages, attachFile)
                .where(martProduct.mart.martID.eq(martID)) // 마트 필터
                .where(martProduct.delFlag.eq(false)) // 삭제되지 않은 MartProduct
                .where(attachFile.ord.eq(0)) // 첫 번째 이미지
                .where(builder); // 검색 조건

        // 페이징 적용
        getQuerydsl().applyPagination(pageable, query);

        List<MartProduct> martProductList = query.fetch();
        long total = query.fetchCount();

        // DTO 변환
        List<ProductListDTO> dtoList = martProductList.stream()
                .map(mp -> {
                    Product prod = mp.getProduct();
                    return ProductListDTO.builder()
                            .productID(prod.getProductID())
                            .name(prod.getName())
                            .sku(prod.getSku())
                            .price(prod.getPrice())
                            .thumbnailImage(prod.getAttachImages().isEmpty() ? null : prod.getAttachImages().get(0).getImageURL())
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    public ProductReadDTO readById(Long martID, Long productID) {

        log.info("-------------------read----------");

        QProduct product = QProduct.product;
        QProductImage attachFile = QProductImage.productImage;
        QMartProduct martProduct = QMartProduct.martProduct;

        JPQLQuery<Product> query = from(product)
                .leftJoin(product.attachImages, attachFile).fetchJoin()
                .leftJoin(martProduct).on(martProduct.product.eq(product))
                .where(martProduct.mart.martID.eq(martID))  // 마트 ID 조건 추가
                .where(product.productID.eq(productID))    // 상품 ID 조건
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
