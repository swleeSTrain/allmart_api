package org.sunbong.allmart_api.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.common.util.CustomFileUtil;
import org.sunbong.allmart_api.product.domain.Product;
import org.sunbong.allmart_api.product.dto.ProductAddDTO;
import org.sunbong.allmart_api.product.dto.ProductEditDTO;
import org.sunbong.allmart_api.product.dto.ProductListDTO;
import org.sunbong.allmart_api.product.dto.ProductReadDTO;
import org.sunbong.allmart_api.product.repository.ProductRepository;

import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CustomFileUtil fileUtil;

    // 조회
    public ProductReadDTO readById(Long id) {

        ProductReadDTO result = productRepository.readById(id);

        return result;
    }

    // 리스트
    public PageResponseDTO<ProductListDTO> list(PageRequestDTO pageRequestDTO) {

        PageResponseDTO<ProductListDTO> result = productRepository.list(pageRequestDTO);

        return result;
    }

    // 등록
    public Long register(ProductAddDTO dto) throws Exception {

        Product product = Product.builder()
                .name(dto.getName())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .build();

        // 업로드할 파일이 있을 경우
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {

            List<String> savedFileNames = fileUtil.saveFiles(dto.getFiles());

            savedFileNames.forEach(product::addFile);
        }

        Product savedProduct = productRepository.save(product);

        return savedProduct.getProductID();
    }

    // 삭제
    public Long delete(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with ID: " + id));

        Long productID = product.getProductID();
        productRepository.deleteById(productID);

        return productID;
    }

    // 수정
    public Long edit(Long id, ProductEditDTO dto) throws Exception {

        // 기존 상품 조회
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // 파일 저장 및 삭제 로직 처리
        List<String> filesToAdd = fileUtil.saveFiles(dto.getFiles());
        List<String> filesToDelete = dto.getFilesToDelete() != null ? dto.getFilesToDelete() : List.of(); // null일 때 빈 리스트로 처리

        Product updatedProduct = existingProduct.toBuilder()
                .name(dto.getName())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .build();

        // 파일 업데이트 적용
        updatedProduct.updateFiles(filesToAdd, filesToDelete);

        productRepository.save(updatedProduct);

        return updatedProduct.getProductID();
    }



}
