package org.sunbong.allmart_api.mart.repository.search;

import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.mart.dto.MartListDTO;
import org.sunbong.allmart_api.mart.dto.MartReadDTO;

public interface MartSearch {

    PageResponseDTO<MartListDTO> listWithinRadius(PageRequestDTO pageRequestDTO, double lat, double lng, double radiusKm);

    PageResponseDTO<MartListDTO> list(PageRequestDTO pageRequestDTO);

    MartReadDTO readById(Long martID);
}
