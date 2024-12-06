package org.sunbong.allmart_api.qna.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.qna.domain.Question;
import org.sunbong.allmart_api.qna.repository.search.QuestionSearch;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionSearch {

}
