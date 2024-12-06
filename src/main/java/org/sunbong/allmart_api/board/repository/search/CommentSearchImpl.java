package org.sunbong.allmart_api.board.repository.search;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.board.domain.Comment;
import org.sunbong.allmart_api.board.domain.QComment;
import org.sunbong.allmart_api.board.dto.CommentDTO;
import org.sunbong.allmart_api.board.dto.CommentListDTO;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CommentSearchImpl extends QuerydslRepositorySupport implements CommentSearch {
    public CommentSearchImpl() {
        super(Comment.class);
    }

    @Override
    public PageResponseDTO<CommentDTO> readByCno(Long cno, PageRequestDTO pageRequestDTO) {
        return null;
    }

    @Override
    public PageResponseDTO<CommentListDTO> readByBno(Long bno, PageRequestDTO pageRequestDTO) {
        // Pageable 객체 생성 (PageRequestDTO에서 페이지 정보 및 정렬 정보 가져옴)
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,   // 페이지 번호 (0부터 시작하므로 -1)
                pageRequestDTO.getSize(),       // 페이지 크기
                Sort.by("cno").descending()     // 댓글 번호(cno)로 정렬
        );

        QComment qComment = QComment.comment;

        // JPQL 쿼리 생성 (bno로 필터링)
        JPQLQuery<Comment> query = from(qComment)
                .where(qComment.boardPost.bno.eq(bno));

        // 페이징 처리 적용
        this.getQuerydsl().applyPagination(pageable, query);

        // 쿼리 실행 후 결과 가져오기
        List<Comment> resultList = query.fetch();

        // DTO로 변환 (Entity -> DTO)
        List<CommentListDTO> dtoList = resultList.stream()
                .map(comment -> CommentListDTO.builder()
                        .cno(comment.getCno())
                        .content(comment.getContent())
                        .writer(comment.getWriter())
                        .createTime(comment.getCreateTime())
                        .updateTime(comment.getUpdateTime())
                        .boardPostBno(comment.getBoardPost().getBno())  // 게시물 번호만 포함
                        .build())
                .collect(Collectors.toList());

        // 전체 데이터 개수 구하기
        long total = query.fetchCount();

        // PageResponseDTO로 페이징 정보와 DTO 리스트 반환
        return PageResponseDTO.<CommentListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }



}
