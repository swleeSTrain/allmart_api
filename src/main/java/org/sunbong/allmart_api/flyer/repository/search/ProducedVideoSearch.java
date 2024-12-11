package org.sunbong.allmart_api.flyer.repository.search;

import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.flyer.dto.FlyerListDTO;
import org.sunbong.allmart_api.flyer.dto.FlyerReadDTO;
import org.sunbong.allmart_api.flyer.dto.ProducedVideoListDTO;

public interface ProducedVideoSearch {

    PageResponseDTO<ProducedVideoListDTO> list(PageRequestDTO pageRequestDTO);

    FlyerReadDTO readById(Long flyerID);
}
