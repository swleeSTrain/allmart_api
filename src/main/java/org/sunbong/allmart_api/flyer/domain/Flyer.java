package org.sunbong.allmart_api.flyer.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.sunbong.allmart_api.common.domain.BaseEntity;
import org.sunbong.allmart_api.product.domain.ProductImage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_flyer")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true, exclude = {"attachImages", "audioURL","producedVideo"})
public class Flyer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flyerID;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tbl_flyer_audio")
    @Builder.Default
    @BatchSize(size = 20)
    private Set<String> audioURL = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "tbl_flyer_image")
    @BatchSize(size = 50)
    @Builder.Default
    private List<FlyerImage> attachImages = new ArrayList<>();

    // 1:1 관계 설정
    @OneToOne(mappedBy = "flyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProducedVideo producedVideo;


    public void addImage(String filename) {
        attachImages.add(new FlyerImage(filename, attachImages.size()));
    }

    public void clearImages() {
        attachImages.clear();
    }

    public void addAudioURL(String audioUrl) {
        audioURL.add(audioUrl);
    }

    public void clearAudioURL() {
        audioURL.clear();
    }

    // ProducedVideo 설정 메서드
    public void setProducedVideo(ProducedVideo producedVideo) {
        this.producedVideo = producedVideo;
        producedVideo.setFlyer(this);
    }

}
