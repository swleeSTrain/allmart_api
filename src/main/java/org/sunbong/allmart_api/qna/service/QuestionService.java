package org.sunbong.allmart_api.qna.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;
import org.sunbong.allmart_api.common.util.CustomFileUtil;
import org.sunbong.allmart_api.qna.domain.Question;
import org.sunbong.allmart_api.qna.dto.QnaReadDTO;
import org.sunbong.allmart_api.qna.dto.QuestionAddDTO;
import org.sunbong.allmart_api.qna.dto.QuestionListDTO;
import org.sunbong.allmart_api.qna.repository.AnswerRepository;
import org.sunbong.allmart_api.qna.repository.QuestionRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CustomFileUtil fileUtil;

    // 조회
    public PageResponseDTO<QnaReadDTO> readByQno(Long qno, PageRequestDTO pageRequestDTO) {

        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return answerRepository.readByQno(qno, pageRequestDTO);
    }

    // 질문 리스트
    public PageResponseDTO<QuestionListDTO> list(PageRequestDTO pageRequestDTO) {

        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }

        return questionRepository.questionList(pageRequestDTO);
    }

    // 질문 등록
    public Long registerQuestion(QuestionAddDTO dto) throws IOException {

        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .tags(dto.getTags())
                .build();

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<String> savedFileNames = fileUtil.saveFiles(dto.getFiles());
            for (String fileName : savedFileNames) {
                question.addFile(fileName);
            }
        }

        Question savedQuestion = questionRepository.save(question);
        return savedQuestion.getQno();
    }

    // 질문 삭제
    public Long delete(Long id) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with ID: " + id));

        questionRepository.delete(question);
        return question.getQno();
    }

    // 질문 수정
    public Long editQuestion(Long qno, QuestionAddDTO dto) throws IOException {

        Question question = questionRepository.findById(qno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));

        question.editQuestion(dto.getTitle(), dto.getContent(),
                dto.getWriter(), new HashSet<>(dto.getTags()));

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            List<String> savedFileNames = fileUtil.saveFiles(dto.getFiles());
            for (String fileName : savedFileNames) {
                question.addFile(fileName);
            }
        }

        questionRepository.save(question);

        return question.getQno();
    }
}
