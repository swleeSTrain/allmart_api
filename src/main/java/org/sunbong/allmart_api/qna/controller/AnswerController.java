package org.sunbong.allmart_api.qna.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.qna.dto.AnswerAddDTO;
import org.sunbong.allmart_api.qna.dto.AnswerListDTO;
import org.sunbong.allmart_api.qna.service.AnswerService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/qna/answer")
@Log4j2
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/{qno}")
    public ResponseEntity<List<AnswerListDTO>> getAnswersByQuestion(@PathVariable Long qno) {
        List<AnswerListDTO> answers = answerService.getAnswersByQuestion(qno);
        return ResponseEntity.ok(answers);
    }

    @PostMapping("add")
    public ResponseEntity<Long> registerAnswer(@RequestBody AnswerAddDTO dto) {

        Long ano = answerService.registerAnswer(dto);

        return ResponseEntity.ok(ano);
    }

    @DeleteMapping("/{ano}")
    public ResponseEntity<Long> delete(@PathVariable Long ano) {

        Long deletedAno = answerService.delete(ano);

        return ResponseEntity.ok(deletedAno);
    }

    @PutMapping("/{ano}")
    public ResponseEntity<Long> updateAnswer(
            @PathVariable Long ano,
            @RequestBody AnswerAddDTO dto) {

        Long updatedAno = answerService.updateAnswer(ano, dto);

        return ResponseEntity.ok(updatedAno);
    }
}
