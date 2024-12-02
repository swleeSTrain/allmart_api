package org.sunbong.allmart_api.qna.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.qna.domain.Answer;
import org.sunbong.allmart_api.qna.repository.search.AnswerSearch;

public interface AnswerRepository extends JpaRepository<Answer, Long>, AnswerSearch {


}
