package org.sunbong.allmart_api.banner.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_banner")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 200)
    private String link;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private String image; // 이미지 파일 경로 또는 URL

    @Column(nullable = false)
    private Long martId;
}

