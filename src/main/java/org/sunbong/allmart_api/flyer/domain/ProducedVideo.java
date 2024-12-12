package org.sunbong.allmart_api.flyer.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_produced_video")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ProducedVideo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoID;

    @Column(length = 100, nullable = false)
    private String fileName; // 영상 파일 이름

    @Column(nullable = false)
    private String size; // 파일 크기 (예: "10MB")

    @Column(nullable = false)
    private LocalDate uploadDate; // 업로드 날짜

    @Column(nullable = false)
    private String link; // 유튜브 숏폼 링크 또는 보기 링크

    @Column(nullable = false)
    private String originalFile; // 원본 파일 링크

    @Column(length = 500)
    private String memo; // 메모 (비고)


    // 1:1 관계 설정
    @OneToOne
    @JoinColumn(name = "flyer_id")
    @JsonBackReference
    private Flyer flyer;

    public void setFlyer(Flyer flyer) {
        this.flyer = flyer;
    }
}