package org.sunbong.allmart_api.qna.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.qna.domain.*;
import org.sunbong.allmart_api.qna.dto.AnswerListDTO;
import org.sunbong.allmart_api.qna.dto.QnaReadDTO;
import org.sunbong.allmart_api.qna.repository.search.AnswerSearch;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class AnswerSearchImpl extends QuerydslRepositorySupport implements AnswerSearch {

    public AnswerSearchImpl() {
        super(Answer.class);
    }

    @Override
    public QnaReadDTO readByQno(Long qno) {

        log.info("-------------------Read By Qno----------- qno: {}", qno);

        QQuestion question = QQuestion.question;
        QAnswer answer = QAnswer.answer;
        QQuestionAttachFile attachFile = QQuestionAttachFile.questionAttachFile;

        // 단일 질문 쿼리 작성
        JPQLQuery<Question> query = from(question)
                .leftJoin(question.attachFiles, attachFile).fetchJoin()
                .leftJoin(question.tags).fetchJoin()
                .leftJoin(answer).on(answer.question.eq(question))
                .where(question.qno.eq(qno));

        JPQLQuery<Tuple> tupleJPQLQuery =
                query.select(question, answer, attachFile);

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        if (tupleList.isEmpty()) {
            throw new IllegalArgumentException("No question found with qno: " + qno);
        }

        // 첫 번째 Tuple에서 Question 및 관련 데이터를 추출
        Tuple firstTuple = tupleList.get(0);
        Question q = firstTuple.get(question);

        // AnswerList 생성
        List<AnswerListDTO> answerList = tupleList.stream()
                .map(tuple -> tuple.get(answer))
                .filter(a -> a != null) // Answer가 null이 아닌 경우만 처리
                .map(a -> AnswerListDTO.builder()
                        .ano(a.getAno())
                        .content(a.getContent())
                        .writer(a.getWriter())
                        .createdDate(a.getCreatedDate())
                        .modifiedDate(a.getModifiedDate())
                        .build())
                .collect(Collectors.toList());

        // AttachFiles 추출
        Set<String> attachFileNames = q.getAttachFiles() != null
                ? q.getAttachFiles().stream()
                .map(QuestionAttachFile::getFileName)
                .collect(Collectors.toSet())
                : Set.of();

        // QnaReadDTO 생성 및 반환
        QnaReadDTO dto = QnaReadDTO.builder()
                .qno(q.getQno())
                .title(q.getTitle())
                .content(q.getContent())
                .writer(q.getWriter())
                .createdDate(q.getCreatedDate())
                .modifiedDate(q.getModifiedDate())
                .tags(q.getTags())
                .attachFiles(attachFileNames)
//                .answers(answerList)
                .answers(answerList.isEmpty() ? List.of() : answerList) // 답변이 없으면 빈 리스트
                .build();

        log.info("DTO Created: {}", dto);

        return dto;
    }
}
