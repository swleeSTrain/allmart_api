package org.sunbong.allmart_api.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.sunbong.allmart_api.board.domain.BoardPost;
import org.sunbong.allmart_api.board.dto.BoardPostAddDTO;
import org.sunbong.allmart_api.board.dto.BoardPostListDTO;
import org.sunbong.allmart_api.board.dto.BoardPostReadDTO;
import org.sunbong.allmart_api.board.repository.BoardPostRepository;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.common.exception.CommonExceptions;

import java.util.List;
import java.util.Map;

import static org.sunbong.allmart_api.board.util.UploadUtil.saveFileAndCreateThumbnail;

@Service
@RequiredArgsConstructor
public class BoardPostService {

    private final BoardPostRepository boardRepository;

    public PageResponseDTO<BoardPostReadDTO> readByBno(Long bno, PageRequestDTO pageRequestDTO) {
        // 페이지 번호가 0보다 작으면 예외 발생
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }
        PageResponseDTO<BoardPostReadDTO> result = boardRepository.readByBno(bno, pageRequestDTO);
        return result;
    }

    public PageResponseDTO<BoardPostListDTO> listByBno(PageRequestDTO pageRequestDTO) {
        if (pageRequestDTO.getPage() < 0) {
            throw CommonExceptions.LIST_ERROR.get();
        }
        PageResponseDTO<BoardPostListDTO> result = boardRepository.listByBno(pageRequestDTO);
        return result;
    }

    public BoardPostAddDTO newPost(BoardPostAddDTO boardPostDTO,
                                   @RequestParam(required = false) List<MultipartFile> files) {

        BoardPost post = BoardPost.builder()
                .title(boardPostDTO.getTitle())
                .writer(boardPostDTO.getWriter())
                .content(boardPostDTO.getContent())
                .isPinned(boardPostDTO.isPinned()) // 상단 고정 여부 추가
                .build();


        if(boardPostDTO.getFiles() != null && !boardPostDTO.getFiles().isEmpty()) {
            for (MultipartFile file : files) {
                Map<String, String> fileData = saveFileAndCreateThumbnail(file);
                String savedFileName = fileData.get("savedFileName");
                String thumbnailFileName = fileData.get("thumbnailFileName");
                post.addBoardAttachFile(savedFileName); // Notice 엔티티에 파일 추가
                post.addBoardAttachFile(thumbnailFileName);
            }
        }

        boardRepository.save(post);
        return boardPostDTO;
    }

    public BoardPostAddDTO updatePost(Long bno, BoardPostAddDTO boardPostDTO,
                                      @RequestParam(required = false) List<MultipartFile> files) {
        BoardPost post = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

//        BoardPost originpost = BoardPost.builder()
//                .bno(post.getBno())
//                .title(post.getTitle())
//                .author(post.getAuthor())
//                .content(post.getContent())
//                .createTime(post.getCreateTime())
//                .build();
        BoardPost update = post.toBuilder()
                .title(boardPostDTO.getTitle())
                .content(boardPostDTO.getContent())
                .isPinned(boardPostDTO.isPinned()) // 상단 고정 여부 추가
                .build();

        if(boardPostDTO.getFiles() != null && !boardPostDTO.getFiles().isEmpty()) {
            for (MultipartFile file : files) {
                Map<String, String> fileData = saveFileAndCreateThumbnail(file);
                String savedFileName = fileData.get("savedFileName");
                String thumbnailFileName = fileData.get("thumbnailFileName");
                update.addBoardAttachFile(savedFileName); // Notice 엔티티에 파일 추가
                update.addBoardAttachFile(thumbnailFileName);
            }
        }
        boardRepository.save(update);
        return boardPostDTO;
    }


    // 게시글 소프트 삭제
    public String softDeletePost(Long bno) {
        // 게시글을 ID로 조회
        BoardPost post = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));


        // 삭제를 true로 바꿈
        BoardPost delete = BoardPost.builder()
                .bno(post.getBno())
                .title(post.getTitle())
                .writer(post.getWriter())
                .content(post.getContent())
                .delflag(true)
                .build();


        //저장
        boardRepository.save(delete);
        return delete.getBno() + "번 게시물이 삭제되었습니다.";
    }
    // 게시글 상단 고정/해제
    public void togglePinPost(Long bno) {
        BoardPost post = boardRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        BoardPost updatedPost = post.toBuilder()
                .isPinned(!post.isPinned())
                .build();

        boardRepository.save(updatedPost);
    }
}
