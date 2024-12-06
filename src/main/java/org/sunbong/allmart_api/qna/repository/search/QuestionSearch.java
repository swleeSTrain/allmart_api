package org.sunbong.allmart_api.qna.repository.search;


import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.qna.dto.QuestionListDTO;

public interface QuestionSearch {

    PageResponseDTO<QuestionListDTO> questionList(PageRequestDTO pageRequestDTO);

}
