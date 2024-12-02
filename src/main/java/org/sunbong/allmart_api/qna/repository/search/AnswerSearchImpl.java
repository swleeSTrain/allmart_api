package org.sunbong.allmart_api.qna.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.qna.domain.*;
import org.sunbong.allmart_api.qna.dto.AnswerListDTO;
import org.sunbong.allmart_api.qna.dto.QnaReadDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class AnswerSearchImpl extends QuerydslRepositorySupport implements AnswerSearch {

    public AnswerSearchImpl() {
        super(Answer.class);
    }

    @Override
    public PageResponseDTO<QnaReadDTO> readByQno(Long qno, PageRequestDTO pageRequestDTO) {

        log.info("-------------------Read By Qno-----------");

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("createdDate").descending()
        );

        QQuestion question = QQuestion.question;
        QAnswer answer = QAnswer.answer;
        QQuestionAttachFile attachFile = QQuestionAttachFile.questionAttachFile;

        JPQLQuery<Question> query = from(question)
                .leftJoin(question.attachFiles, attachFile).fetchJoin()
                .leftJoin(question.tags).fetchJoin()
                .leftJoin(answer).on(answer.question.eq(question))
                .where(question.qno.eq(qno));

        // 페이징 적용
        getQuerydsl().applyPagination(pageable, query);

        JPQLQuery<Tuple> tupleJPQLQuery =
                query.select(question, answer, attachFile);

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        long total = tupleJPQLQuery.fetchCount();

        log.info(tupleList);
        log.info("======================================================");

        // QnaReadDTO 생성
        List<QnaReadDTO> dtoList = tupleList.stream()
                .map(tuple -> {
                    Question q = tuple.get(question);
                    Answer a = tuple.get(answer);

                    List<AnswerListDTO> answerList = (a != null) ? List.of(AnswerListDTO.builder()
                            .ano(a.getAno())
                            .content(a.getContent())
                            .writer(a.getWriter())
                            .createdDate(a.getCreatedDate())
                            .modifiedDate(a.getModifiedDate())
                            .build()) : List.of();

                    // QuestionAttachFile의 fileName만 추출하여 Set<String>으로 변환
                    Set<String> attachFileNames = (q.getAttachFiles() != null) ?
                            q.getAttachFiles().stream()
                                    .map(QuestionAttachFile::getFileName)
                                    .collect(Collectors.toSet()) : Set.of();

                    return QnaReadDTO.builder()
                            .qno(q.getQno())
                            .title(q.getTitle())
                            .content(q.getContent())
                            .writer(q.getWriter())
                            .createdDate(q.getCreatedDate())
                            .modifiedDate(q.getModifiedDate())
                            .tags(q.getTags())
                            .attachFiles(attachFileNames)
                            .answers(answerList)
                            .build();
                })
                .collect(Collectors.toList());


        // 결과 반환
        return PageResponseDTO.<QnaReadDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();

    }
}
