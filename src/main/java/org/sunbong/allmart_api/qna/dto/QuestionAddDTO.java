package org.sunbong.allmart_api.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAddDTO {

    private String title;
    private String content;
    private String writer;
    private Set<String> tags;

    // 저장된 파일명 (파일 업로드 후 서버에 저장된 파일 이름)
    private Set<String> attachFiles;


    // 업로드할 파일 (사용자가 실제로 업로드한 파일 목록)
    private List<MultipartFile> files;

    private List<String> deleteFiles;  // 삭제할 기존 파일

}
