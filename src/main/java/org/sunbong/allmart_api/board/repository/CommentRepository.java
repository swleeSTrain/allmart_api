package org.sunbong.allmart_api.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.board.domain.Comment;
import org.sunbong.allmart_api.board.repository.search.CommentSearch;

public interface CommentRepository extends JpaRepository<Comment, Long> , CommentSearch {
}
