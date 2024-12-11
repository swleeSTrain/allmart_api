package org.sunbong.allmart_api.qna.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnswerEditDTO {

    private String content;
    private String writer;
    private Long qno;
}
