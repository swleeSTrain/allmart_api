package org.sunbong.allmart_api.qna.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.qna.domain.QAnswer;
import org.sunbong.allmart_api.qna.domain.QQuestion;
import org.sunbong.allmart_api.qna.domain.Question;
import org.sunbong.allmart_api.qna.dto.QuestionListDTO;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class QuestionSearchImpl extends QuerydslRepositorySupport implements QuestionSearch {

    public QuestionSearchImpl() {
        super(Question.class);
    }

    @Override
    public PageResponseDTO<QuestionListDTO> questionList(PageRequestDTO pageRequestDTO) {

        log.info("-------------------list with search-----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("qno").descending()
        );

        QQuestion question = QQuestion.question;
        QAnswer answer = QAnswer.answer;

        // 질문 목록 조회 쿼리 답변을 leftjoin 하고 tags를 fetch join으로
        JPQLQuery<Question> query = from(question)
                .leftJoin(question.tags).fetchJoin()
                .leftJoin(answer).on(answer.question.eq(question));

        BooleanBuilder builder = new BooleanBuilder();
        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();

        // 키워드 및 타입에 대한 검색 조건 추가
        if (keyword != null && type != null) {
            if (type.contains("title")) {
                builder.or(question.title.containsIgnoreCase(keyword));
            }
            if (type.contains("content")) {
                builder.or(question.content.containsIgnoreCase(keyword));
            }
            if (type.contains("writer")) {
                builder.or(question.writer.containsIgnoreCase(keyword));
            }
        }

        // 태그에 대한 검색 조건 추가 (OR 조건으로 태그 검색)
        if (pageRequestDTO.getTags() != null && !pageRequestDTO.getTags().isEmpty()) {

            BooleanBuilder tagBuilder = new BooleanBuilder();

            pageRequestDTO.getTags().forEach(tag -> {
                tagBuilder.or(question.tags.any().eq(tag));
            });
            builder.and(tagBuilder);
        }

        // JPQL 쿼리 구성
        query.where(builder);

        //group by
        query.groupBy(question);

        // 총 개수 계산
        getQuerydsl().applyPagination(pageable, query);

        JPQLQuery<Tuple> tupleJPQLQuery =
                query.select(question, answer.count());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        long total = tupleJPQLQuery.fetchCount();

        log.info(tupleList);

        log.info("======================================================");

        // QuestionListDTO 생성
        List<QuestionListDTO> dtoList = tupleList.stream()
                .map(tuple -> {
                    Question q = tuple.get(question);
                    long answerCount = tuple.get(answer.count());

                    return QuestionListDTO.builder()
                            .qno(q.getQno())
                            .title(q.getTitle())
                            .content(q.getContent())
                            .writer(q.getWriter())
                            .createdDate(q.getCreatedDate())
                            .modifiedDate(q.getModifiedDate())
                            .answerCount(answerCount)
                            .tags(q.getTags())
                            .build();
                })
                .collect(Collectors.toList());

        log.info("tag 문제?");
        log.info("========================================");

        // 결과 반환
        return PageResponseDTO.<QuestionListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();

    }

}
