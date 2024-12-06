package org.sunbong.allmart_api.board.repository.search;

import org.sunbong.allmart_api.board.dto.BoardPostListDTO;
import org.sunbong.allmart_api.board.dto.BoardPostReadDTO;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;

public interface BoardPostSearch {

    PageResponseDTO<BoardPostListDTO> listByBno(PageRequestDTO requestDTO);
    PageResponseDTO<BoardPostReadDTO> readByBno(Long bno, PageRequestDTO pageRequestDTO);
}
