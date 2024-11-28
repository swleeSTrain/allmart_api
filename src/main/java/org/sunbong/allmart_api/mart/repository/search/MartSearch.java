package org.sunbong.allmart_api.mart.repository.search;

import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.mart.dto.MartListDTO;

public interface MartSearch {

    PageResponseDTO<MartListDTO> list(PageRequestDTO pageRequestDTO);
}
