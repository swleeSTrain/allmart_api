package org.sunbong.allmart_api.qna.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.qna.domain.Answer;
import org.sunbong.allmart_api.qna.domain.Question;
import org.sunbong.allmart_api.qna.dto.AnswerAddDTO;
import org.sunbong.allmart_api.qna.dto.AnswerListDTO;
import org.sunbong.allmart_api.qna.dto.QnaReadDTO;
import org.sunbong.allmart_api.qna.repository.AnswerRepository;
import org.sunbong.allmart_api.qna.repository.QuestionRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
///=======================================
    public QnaReadDTO getQuestionWithAnswers(Long qno) {
        QnaReadDTO dto = answerRepository.readByQno(qno);

        // 방어적 처리: 답변이 없는 경우 빈 리스트를 반환
        if (dto.getAnswers() == null) {
            dto.setAnswers(List.of());
        }

        return dto;
    }
//=========================================

    public List<AnswerListDTO> getAnswersByQuestion(Long qno) {
        List<Answer> answers = answerRepository.findByQuestionQno(qno);
        return answers.stream()
                .map(answer -> AnswerListDTO.builder()
                        .ano(answer.getAno())
                        .content(answer.getContent())
                        .writer(answer.getWriter())
                        .createdDate(answer.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }
    // 답변 등록
    @Transactional
    public Long registerAnswer(AnswerAddDTO dto) {

        // 질문(Question) 찾기
        Question question = questionRepository.findById(dto.getQno())
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));


        Answer answer = Answer.builder()
                .content(dto.getContent())
                .writer(dto.getWriter())
                .question(question)  // 해당 답변이 속한 질문 설정
                .build();

        Answer savedAnswer = answerRepository.save(answer);

        return savedAnswer.getAno();  // 등록된 답변의 ID 반환
    }

    // 답변 삭제
    public Long delete(Long id) {
        // 존재 여부 확인 후 삭제
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with ID: " + id));

        // 질문 ID 반환하기 전에 삭제
        Long qno = answer.getAno();
        answerRepository.delete(answer);

        return qno; // 삭제된 질문의 ID 반환
    }

    // 답변 수정
    public Long updateAnswer(Long ano, AnswerAddDTO dto) {
        Answer answer = answerRepository.findById(ano)
                .orElseThrow(() -> new IllegalArgumentException("Invalid answer ID"));

        answer = answer.toBuilder()
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();

        Answer updatedAnswer = answerRepository.save(answer);

        return updatedAnswer.getAno(); // 업데이트된 답변의 ID 반환
    }


}
