package org.sunbong.allmart_api.board.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.board.domain.BoardAttachFile;
import org.sunbong.allmart_api.board.domain.BoardPost;
import org.sunbong.allmart_api.board.domain.QBoardAttachFile;
import org.sunbong.allmart_api.board.domain.QBoardPost;
import org.sunbong.allmart_api.board.dto.BoardPostListDTO;
import org.sunbong.allmart_api.board.dto.BoardPostReadDTO;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
public class BoardPostSearchImpl extends QuerydslRepositorySupport implements BoardPostSearch {
    public BoardPostSearchImpl() {
        super(BoardPost.class);
    }

    @Override
    public PageResponseDTO<BoardPostListDTO> listByBno(PageRequestDTO pageRequestDTO) {
        QBoardPost qBoardPost = QBoardPost.boardPost;

        // 상단 고정 게시글 쿼리 (검색 조건 없음)
        JPQLQuery<BoardPost> pinnedQuery = from(qBoardPost)
                .where(qBoardPost.delflag.eq(false)
                        .and(qBoardPost.isPinned.eq(true)))
                .orderBy(qBoardPost.bno.desc());

        // 일반 게시글 쿼리
        JPQLQuery<BoardPost> normalQuery = from(qBoardPost)
                .where(qBoardPost.delflag.eq(false)
                        .and(qBoardPost.isPinned.eq(false)));

        // 검색 조건 추가 (일반 게시글에만 적용)
        String keyword = pageRequestDTO.getKeyword();
        String type = pageRequestDTO.getType();
        if (keyword != null && type != null) {
            BooleanExpression searchPredicate = createSearchPredicate(qBoardPost, type, keyword);
            normalQuery.where(searchPredicate);
        }

        // 정렬 및 페이징 적용
        normalQuery.orderBy(qBoardPost.bno.desc());
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize()
        );
        this.getQuerydsl().applyPagination(pageable, normalQuery);

        // 결과 조회
        List<BoardPost> pinnedList = pinnedQuery.fetch();
        List<BoardPost> normalList = normalQuery.fetch();
        List<BoardPost> resultList = new ArrayList<>(pinnedList);
        resultList.addAll(normalList);

        List<BoardPostListDTO> dtoList = resultList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long total = pinnedQuery.fetchCount() + normalQuery.fetchCount();

        return PageResponseDTO.<BoardPostListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    private BooleanExpression createSearchPredicate(QBoardPost qBoardPost, String type, String keyword) {
        if (type.equals("title")) {
            return qBoardPost.title.containsIgnoreCase(keyword);
        } else if (type.equals("content")) {
            return qBoardPost.content.containsIgnoreCase(keyword);
        } else if (type.equals("writer")) {
            return qBoardPost.writer.containsIgnoreCase(keyword);
        } else if (type.equals("all")) {
            return qBoardPost.title.containsIgnoreCase(keyword)
                    .or(qBoardPost.content.containsIgnoreCase(keyword))
                    .or(qBoardPost.writer.containsIgnoreCase(keyword));
        }
        return null;
    }

    private BoardPostListDTO convertToDTO(BoardPost post) {
        return BoardPostListDTO.builder()
                .bno(post.getBno())
                .title(post.getTitle())
                .writer(post.getWriter())
                .fileName(post.getBoardAttachFiles()
                        .stream()
                        .map(BoardAttachFile::getFileName)
                        .filter(fileName -> fileName.contains("/s_"))
                        .findFirst()
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList()))
                .createDate(post.getCreatedDate())
                .modifyDate(post.getModifiedDate())
                .isPinned(post.isPinned())
                .build();
    }

    @Override
    public PageResponseDTO<BoardPostReadDTO> readByBno(Long bno, PageRequestDTO pageRequestDTO) {
        log.info("Reading board post by bno: {}", bno);

        QBoardPost qBoardPost = QBoardPost.boardPost;
        QBoardAttachFile qBoardAttachFile = QBoardAttachFile.boardAttachFile;

        // 필요한 데이터만 선택하여 조회
        JPQLQuery<Tuple> query = from(qBoardPost)
                .leftJoin(qBoardPost.boardAttachFiles, qBoardAttachFile)
                .where(qBoardPost.bno.eq(bno)
                        .and(qBoardPost.delflag.eq(false)))
                .select(qBoardPost.bno, qBoardPost.title, qBoardPost.writer, qBoardPost.content,
                        qBoardPost.createdDate, qBoardPost.modifiedDate, qBoardAttachFile.fileName,
                        qBoardPost.isPinned);

        List<Tuple> resultList = query.fetch();

        // DTO 변환
        List<BoardPostReadDTO> dtoList = resultList.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(qBoardPost.bno),
                        Collectors.collectingAndThen(Collectors.toList(), tuples -> {
                            BoardPostReadDTO.BoardPostReadDTOBuilder builder = BoardPostReadDTO.builder()
                                    .bno(tuples.get(0).get(qBoardPost.bno))
                                    .title(tuples.get(0).get(qBoardPost.title))
                                    .writer(tuples.get(0).get(qBoardPost.writer))
                                    .content(tuples.get(0).get(qBoardPost.content))
                                    .createDate(tuples.get(0).get(qBoardPost.createdDate))
                                    .modifyDate(tuples.get(0).get(qBoardPost.modifiedDate))
                                    .isPinned(tuples.get(0).get(qBoardPost.isPinned));

                            List<String> filenames = tuples.stream()
                                    .map(tuple -> tuple.get(qBoardAttachFile.fileName))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            return builder.filename(filenames).build();
                        })
                ))
                .values().stream().collect(Collectors.toList());

        return PageResponseDTO.<BoardPostReadDTO>withAll()
                .dtoList(dtoList)
                .totalCount(dtoList.size())
                .pageRequestDTO(pageRequestDTO)
                .build();
    }


}
