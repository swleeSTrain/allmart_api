package org.sunbong.allmart_api.board.util;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Log4j2
public class UploadUtil {
    private final static String folder = "C:\\upload\\";

    public static Map<String, String> saveFileAndCreateThumbnail(MultipartFile file) {
        // 원본 파일명
        String originalFileName = file.getOriginalFilename();
        // 고유한 파일명 생성
        String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // 저장할 파일 객체 생성
        File saveFile = new File(folder, savedFileName);
        if (!saveFile.getParentFile().exists()) {
            boolean created = saveFile.getParentFile().mkdirs();
            if (!created) {
                throw new RuntimeException("업로드 폴더를 생성할 수 없습니다: " + folder);
            }
        }
        // 결과를 저장할 Map 생성
        Map<String, String> result = new HashMap<>();

        try {
            // 파일을 저장
            file.transferTo(saveFile);
            log.info("파일 저장 완료: " + savedFileName);

            // 결과 Map에 저장된 파일명 추가
            result.put("savedFileName", savedFileName);

            // 썸네일 파일명 생성
            String thumbnailFileName = "s_" + savedFileName;

            // 썸네일 생성 (160x160 크기)
            Thumbnails.of(saveFile)
                    .size(160, 160)
                    .toFile(new File(folder, thumbnailFileName));

            log.info("썸네일 생성 완료: " + thumbnailFileName);

            // 결과 Map에 썸네일 파일명 추가
            result.put("thumbnailFileName", thumbnailFileName);

            return result;  // 저장된 파일 이름과 썸네일 파일 이름 반환
        } catch (IOException e) {
            log.error("파일 또는 썸네일 처리 중 오류 발생: " + originalFileName, e);
            throw new RuntimeException("파일 또는 썸네일 처리 실패", e);
        }
    }

}