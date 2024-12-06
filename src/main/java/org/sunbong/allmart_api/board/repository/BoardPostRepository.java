package org.sunbong.allmart_api.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunbong.allmart_api.board.domain.BoardPost;
import org.sunbong.allmart_api.board.repository.search.BoardPostSearch;
@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long>, BoardPostSearch {
}
