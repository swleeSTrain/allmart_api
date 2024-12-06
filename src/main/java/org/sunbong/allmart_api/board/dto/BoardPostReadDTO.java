package org.sunbong.allmart_api.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardPostReadDTO {
    private Long bno;

    private String title;

    private String writer;

    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> filename; // 첨부파일 목록을 문자열 리스트로 처리
    private List<String> fileUrls;
    private boolean isPinned;

}
