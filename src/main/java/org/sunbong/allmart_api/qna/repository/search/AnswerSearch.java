package org.sunbong.allmart_api.qna.repository.search;



import org.sunbong.allmart_api.qna.dto.QnaReadDTO;

public interface AnswerSearch {

    QnaReadDTO readByQno(Long qno); // 단일 질문 조회
}
