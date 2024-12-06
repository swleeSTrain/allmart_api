package org.sunbong.allmart_api.board.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "boardPost")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cno;
    private String writer;
    private String content;



    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "board_bno", referencedColumnName = "bno")
            // 필요에 따라 추가적인 컬럼을 여기에 지정합니다.
    })
    private BoardPost boardPost;

}
