package org.sunbong.allmart_api.qna.repository.search;


import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.qna.dto.QnaReadDTO;

public interface AnswerSearch {

    PageResponseDTO<QnaReadDTO> readByQno(Long qno, PageRequestDTO pageRequestDTO);
}
