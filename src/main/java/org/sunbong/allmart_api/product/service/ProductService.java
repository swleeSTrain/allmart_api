package org.sunbong.allmart_api.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.sunbong.allmart_api.category.domain.Category;
import org.sunbong.allmart_api.category.repository.CategoryRepository;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.util.CustomFileUtil;
import org.sunbong.allmart_api.elasticsearch.ElasticSearchService;
import org.sunbong.allmart_api.inventory.domain.Inventory;
import org.sunbong.allmart_api.inventory.repository.InventoryRepository;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.domain.MartProduct;
import org.sunbong.allmart_api.mart.repository.MartProductRepository;
import org.sunbong.allmart_api.mart.repository.MartRepository;
import org.sunbong.allmart_api.product.domain.Product;
import org.sunbong.allmart_api.product.dto.ProductAddDTO;
import org.sunbong.allmart_api.product.dto.ProductEditDTO;
import org.sunbong.allmart_api.product.dto.ProductListDTO;
import org.sunbong.allmart_api.product.dto.ProductReadDTO;
import org.sunbong.allmart_api.product.repository.ProductRepository;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final MartRepository martRepository;
    private final MartProductRepository martProductRepository;
    private final CustomFileUtil fileUtil;

    private final ElasticSearchService elasticSearchService;

    // 엘라스틱서치
    public PageResponseDTO<ProductListDTO> search(Long martID, String query, PageRequestDTO pageRequestDTO) {

        List<String> skuList = elasticSearchService.getSKUsFromQuery(query);

        if (skuList.isEmpty()) {
            log.warn("No SKUs found for query: {}", query);
            return new PageResponseDTO<>(Collections.emptyList(), pageRequestDTO, 0); // 빈 결과 반환
        }

        return productRepository.searchBySKU(martID, skuList, pageRequestDTO);
    }


    // 조회
    public ProductReadDTO readById(Long martID, Long productID) {

        ProductReadDTO result = productRepository.readById(martID, productID);

        return result;
    }

    // 리스트
    public PageResponseDTO<ProductListDTO> list(Long martID, PageRequestDTO pageRequestDTO) {

        PageResponseDTO<ProductListDTO> result = productRepository.list(martID, pageRequestDTO);

        return result;
    }

    // 등록
    public Long register(Long martID, ProductAddDTO dto) throws Exception {

        // 마트 조회하기
        Mart mart = martRepository.findById(martID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mart ID"));

        // 카테고리 ID로 카테고리 찾기
        Category category = categoryRepository.findById(dto.getCategoryID())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        // Product 객체 생성
        Product product = Product.builder()
                .name(dto.getName())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .category(category)  // 카테고리 설정
                .build();

        // 업로드할 파일이 있을 경우
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<String> savedFileNames = fileUtil.saveFiles(dto.getFiles());
            savedFileNames.forEach(product::addImage);
        }

        // Product 저장
        Product savedProduct = productRepository.save(product);

        // MartProduct 엔티티 생성 및 저장
        MartProduct martProduct = MartProduct.builder()
                .mart(mart)
                .product(savedProduct)
                .build();

        martProductRepository.save(martProduct);

        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(0) // 기본값으로 초기화
                .inStock(1)
                .build();

        inventoryRepository.save(inventory);

        // Elasticsearch에 상품 이름 인덱싱
        elasticSearchService.indexProduct(dto.getName(), dto.getSku());

        return savedProduct.getProductID();
    }


    // 소프트 삭제
    public Long delete(Long martID, Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        // 연관된 Inventory 소프트 딜리트 처리
        Inventory inventory = inventoryRepository.findByProduct(product);
        if (inventory != null) {
            inventory.softDelete();
            inventoryRepository.save(inventory);
        }

        // 연관된 MartProduct 소프트 딜리트 처리
        MartProduct martProduct = martProductRepository.findByMartMartIDAndProductProductID(martID, id);
        if (martProduct != null) {
            martProduct.softDelete(); // 삭제 플래그 설정
            martProductRepository.save(martProduct);
        }


        // Product 소프트 딜리트 처리
        product.softDelete();
        productRepository.save(product);

        return product.getProductID();
    }

    // 수정
    public Long edit(Long martID, Long productID, ProductEditDTO dto) throws Exception {

        // 마트 조회
        Mart mart = martRepository.findById(martID)
                .orElseThrow(() -> new IllegalArgumentException("Mart not found with ID: " + martID));

        // 기존 상품 조회
        Product existingProduct = productRepository.findById(productID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        // 중복 체크
        if (!existingProduct.getName().equals(dto.getName())) {
            validateDuplicate(dto.getName());
        }

        // 카테고리 변경 처리
        if (dto.getCategoryID() != null) {
            Category newCategory = categoryRepository.findById(dto.getCategoryID())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            existingProduct = existingProduct.toBuilder().category(newCategory).build();
        }

        // 기존 파일 처리
        List<String> retainedFiles = dto.getExistingFileNames();

        if (retainedFiles != null) {
            // 기존 파일 중 삭제된 파일 처리 (retainedFiles에 포함되지 않은 파일 삭제)
            existingProduct.getAttachImages()
                    .removeIf(file -> !retainedFiles.contains(file.getImageURL()));
        } else {
            // retainedFiles가 null이면 모든 파일을 삭제
            existingProduct.clearImages();
        }

        // 새 파일 저장
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            // 파일 저장 로직 확인: 파일이 제대로 저장되고 경로가 반환되는지 확인
            List<String> newFileNames = fileUtil.saveFiles(dto.getFiles());

            // 새로 저장된 파일들 addFile을 통해 기존 Product에 추가
            newFileNames.forEach(existingProduct::addImage);
        }

        // 상품 정보 업데이트
        Product updatedProduct = existingProduct.toBuilder()
                .name(dto.getName() != null ? dto.getName() : existingProduct.getName())
                .sku(dto.getSku() != null ? dto.getSku() : existingProduct.getSku())
                .price(dto.getPrice() != null ? dto.getPrice() : existingProduct.getPrice())
                .build();

        // 업데이트된 상품 저장
        productRepository.save(updatedProduct);

        // 마트와의 연관을 처리
        MartProduct martProduct = martProductRepository.findByMartMartIDAndProductProductID(martID, productID);
        if (martProduct == null) {
            martProduct = MartProduct.builder()
                    .mart(mart)
                    .product(updatedProduct)
                    .build();
            martProductRepository.save(martProduct);
        }

        // Elasticsearch에 상품 이름 인덱싱
        elasticSearchService.indexProduct(updatedProduct.getName(), dto.getSku());

        return updatedProduct.getProductID();
    }


    // 중복 체크
    private void validateDuplicate(String name) throws Exception {

        if (productRepository.findByName(name).isPresent()) {
            // 409 상태 코드 반환
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 상품입니다: " + name);
        }
    }


}
