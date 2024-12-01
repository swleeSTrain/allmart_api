package org.sunbong.allmart_api.product.repository.search;

import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.product.dto.ProductListDTO;
import org.sunbong.allmart_api.product.dto.ProductReadDTO;

public interface ProductSearch {

    PageResponseDTO<ProductListDTO> list(Long martID, PageRequestDTO pageRequestDTO);

    ProductReadDTO readById(Long martID, Long productID);

}
