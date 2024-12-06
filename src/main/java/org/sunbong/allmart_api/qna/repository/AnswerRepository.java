package org.sunbong.allmart_api.qna.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.sunbong.allmart_api.qna.domain.Answer;
import org.sunbong.allmart_api.qna.repository.search.AnswerSearch;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long>, AnswerSearch {

    @Query("SELECT a FROM Answer a WHERE a.question.qno = :qno")
    List<Answer> findByQuestionQno(@Param("qno") Long qno);
}
