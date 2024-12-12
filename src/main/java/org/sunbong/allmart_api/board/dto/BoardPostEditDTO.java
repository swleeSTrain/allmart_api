package org.sunbong.allmart_api.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardPostEditDTO {
    private Long bno;

    private String title;

    private String writer;

    private String content;

    private boolean isPinned;

    private List<String> existingFileNames;     // 기존 파일 이름
    private List<MultipartFile> files;  // 새로 업로드된 파일
}
