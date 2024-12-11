package org.sunbong.allmart_api.qna.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sunbong.allmart_api.common.domain.BaseEntity;


@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "question")
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ano;

    private String content;

    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;


}
