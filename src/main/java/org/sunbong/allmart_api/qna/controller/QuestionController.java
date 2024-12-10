package org.sunbong.allmart_api.qna.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.qna.dto.QnaReadDTO;
import org.sunbong.allmart_api.qna.dto.QuestionAddDTO;
import org.sunbong.allmart_api.qna.dto.QuestionEditDTO;
import org.sunbong.allmart_api.qna.dto.QuestionListDTO;
import org.sunbong.allmart_api.qna.service.QuestionService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/qna/question")
@Log4j2
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

//    @GetMapping("/{qno}")
//    public ResponseEntity<QnaReadDTO> readByQuestionId(
//            @PathVariable("qno") Long qno
//    ) {
//        log.info("--------------------------Question Controller read by qno");
//        log.info("============================== qno: " + qno);
//
//        return ResponseEntity.ok(questionService.readByQno(qno));
//    }

    @GetMapping("/{qno}")
    public ResponseEntity<QnaReadDTO> readByQuestionId(
            @PathVariable("qno") Long qno
    ) {
        log.info("Processing request for question ID: {}", qno);

        QnaReadDTO dto = questionService.readByQno(qno);

        if (dto.getAnswers() == null) {
            dto.setAnswers(List.of()); // 답변이 없으면 빈 리스트 반환
        }

        log.info("Fetched question data: {}", dto);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("list")
    public ResponseEntity<PageResponseDTO<QuestionListDTO>> list(
            @Validated PageRequestDTO requestDTO
    ) {

        log.info("--------------------------Qna Controller list");
        log.info("==============================");

        return ResponseEntity.ok(questionService.list(requestDTO));
    }

    @PostMapping(value = "add", consumes = { "multipart/form-data" })
    public ResponseEntity<Long> registerQuestion(@ModelAttribute QuestionAddDTO dto) throws IOException {

        Long qno = questionService.registerQuestion(dto);

        return ResponseEntity.ok(qno);
    }

    @DeleteMapping("/{qno}")
    public ResponseEntity<Long> delete(@PathVariable Long qno) {

        Long deletedQno = questionService.delete(qno);

        return ResponseEntity.ok(deletedQno);
    }

    @PutMapping("/{qno}")
    public ResponseEntity<Long> updateQuestion(
            @PathVariable Long qno,
            @ModelAttribute QuestionEditDTO dto) throws IOException {

        Long updatedQno = questionService.editQuestion(qno, dto);

        return ResponseEntity.ok(updatedQno);
    }



}
