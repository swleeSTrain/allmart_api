package org.sunbong.allmart_api.board.repository.search;

import org.sunbong.allmart_api.board.dto.CommentDTO;
import org.sunbong.allmart_api.board.dto.CommentListDTO;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;

public interface CommentSearch {
    PageResponseDTO<CommentDTO> readByCno(Long cno, PageRequestDTO pageRequestDTO);

    PageResponseDTO<CommentListDTO> readByBno(Long bno, PageRequestDTO pageRequestDTO);
}
