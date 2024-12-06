package org.sunbong.allmart_api.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaReadDTO {

    private Long qno;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Set<String> tags;
    private Set<String> attachFiles;

    // 다중 답변
    private List<AnswerListDTO> answers;;
}
