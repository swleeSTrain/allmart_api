package org.sunbong.allmart_api.board.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString

public class BoardPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;//제목

    private String writer;//작성자

    private String content;//내용

    @Builder.Default
    private boolean isPinned = false;

    @Builder.Default
    private boolean delflag = false;


    @ElementCollection
    @Builder.Default
    @BatchSize(size = 10)
    private Set<BoardAttachFile> boardAttachFiles = new HashSet<>();

    public void addBoardAttachFile(String fileName) {
        boardAttachFiles.add(new BoardAttachFile(boardAttachFiles.size(),fileName) );
    }

    public void clearBoardAttachFiles() { boardAttachFiles.clear();}
}
