package org.sunbong.allmart_api.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.sunbong.allmart_api.board.dto.CommentDTO;
import org.sunbong.allmart_api.board.dto.CommentListDTO;
import org.sunbong.allmart_api.board.service.CommentService;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;

@RestController
@RequestMapping("/api/v1/comment")
@Log4j2
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @GetMapping("{bno}")
    public ResponseEntity<PageResponseDTO<CommentListDTO>> getComments(
            @PathVariable("bno") Long bno,
            @Validated PageRequestDTO pageRequestDTO) {
        log.info("readByBno {}", bno);
        return ResponseEntity.ok(commentService.readByBno(bno, pageRequestDTO));
    }

    @PostMapping("add")
    public ResponseEntity<CommentDTO> addComment(
            @Validated CommentDTO commentDTO
    ){

        return ResponseEntity.ok(commentService.newComment(commentDTO));
    }

    @PutMapping("update/{cno}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable("cno") Long cno,
            @Validated CommentDTO commentDTO
    ){
        return ResponseEntity.ok(commentService.updateComment(cno,commentDTO));
    }

    @DeleteMapping("{cno}")
    public ResponseEntity<Long> deleteComment(
            @PathVariable("cno") Long cno
    ){
        commentService.deleteComment(cno);
        return ResponseEntity.noContent().build();
    }


}
