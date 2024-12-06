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
public class BoardPostListDTO {
    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime createTime;  // 생성날짜
    private LocalDateTime updateTime;  // 수정날짜
    private List<String> fileName;

    private boolean isPinned;


}
