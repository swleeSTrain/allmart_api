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
import org.sunbong.allmart_api.qna.dto.QuestionEditDTO;
import org.sunbong.allmart_api.qna.dto.QuestionListDTO;
import org.sunbong.allmart_api.qna.repository.AnswerRepository;
import org.sunbong.allmart_api.qna.repository.QuestionRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CustomFileUtil fileUtil;

    // 조회
    public QnaReadDTO readByQno(Long qno) {


        return answerRepository.readByQno(qno);
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

    public Long editQuestion(Long qno, QuestionEditDTO dto) throws IOException {
        // 질문 조회
        Question question = questionRepository.findById(qno)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));

        // 질문 정보 업데이트
        question.editQuestion(dto.getTitle(), dto.getContent(), dto.getWriter(), new HashSet<>(dto.getTags()));

        // 기존 파일 처리
        List<String> retainedFiles = dto.getExistingFileNames(); // 유지할 파일 이름 목록

        if (retainedFiles != null) {
            // 기존 파일 중 유지할 파일을 제외한 나머지 삭제
            question.getAttachFiles()
                    .removeIf(file -> !retainedFiles.contains(file.getFileName()));
        } else {
            // retainedFiles가 null인 경우 모든 파일 삭제
            question.clearFiles();
        }

        // 새 파일 저장
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            // 파일 저장
            List<String> newFileNames = fileUtil.saveFiles(dto.getFiles());

            // 새로 저장된 파일들을 Question에 추가
            newFileNames.forEach(question::addFile);
        }

        // 변경 사항 저장
        questionRepository.save(question);

        return question.getQno();

        }
}
