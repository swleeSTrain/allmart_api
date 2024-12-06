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
public class QuestionEditDTO {

    private String title;
    private String content;
    private String writer;
    private Set<String> tags;

    private List<String> existingFileNames;     // 기존 파일 이름
    private List<MultipartFile> files;  // 새로 업로드된 파일

}
