package org.sunbong.allmart_api.qna.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.sunbong.allmart_api.common.domain.BaseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"attachFiles", "tags"})
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qno;

    private String title;

    private String content;

    private String writer;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<String> tags = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 100)
    private List<QuestionAttachFile> attachFiles = new ArrayList<>();


    public void addFile(String filename) {
        attachFiles.add(new QuestionAttachFile(attachFiles.size(), filename));
    }

    public void clearFiles() {
        attachFiles.clear();
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void clearTag() {
        tags.clear();
    }

    public void removeFile(String filename) {
        // 특정 파일 이름과 일치하는 파일을 제거
        attachFiles.removeIf(file -> file.getFileName().equals(filename));

    }

    public void editQuestion(String title, String content, String writer, Set<String> tags) {

        this.title = title;
        this.content = content;
        this.writer = writer;

        // 태그 초기화 후 새로 추가
        this.clearTag();
        if (tags != null) {
            tags.forEach(this::addTag);
        }
    }
}
