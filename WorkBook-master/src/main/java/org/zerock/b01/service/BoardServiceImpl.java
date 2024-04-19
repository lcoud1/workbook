package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.BoardRepository;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService{

    private final ModelMapper modelMapper; // DTO와 엔티티 변환을 해주는 객체

    private final BoardRepository boardRepository; // board CRUD용

    @Override
    public Long register(BoardDTO boardDTO) {

        Board board = modelMapper.map(boardDTO, Board.class);
        // ModelMapper 에서는 map(source, destination) 메소드가 호출되면 source와 destination
        // 타입을 분석하여 매칭 전략 및 기타 설정값에 따라 일치하는 속성을 결정하여 매칭 항목에 대해 데이터를 매핑한다.

        Long bno = boardRepository.save(board).getBno();
        // save() JPA에서 없으면 Insert, 있으면 Update 진행

        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {

        // 주어진 식별 번호로 게시물을 조회
        Optional<Board> result = boardRepository.findByIdWithImages(bno);

        // 조회된 결과가 존재하는지 확인하고, 존재하지 않으면 NoSuchElementException이 발생
        Board board = result.orElseThrow();

        // 조회딘 게시물을 BoardDTO로 매핑
        // ModelMapper를 사용하여 엔티티를 dto로 변환
        BoardDTO boardDTO = entityToDTO(board);

        // 변환된 dto를 반환
        return boardDTO;

        // NoSuchElementException :
        // 클래스의 인스턴스가 발생한 곳에서 요청된 요소나 값이 없을 때 발생
        // 주로 컬렉션에서 요소를 찾을 때 발생하며, 요청한 값이 존재하지 않는 경우에 사용
    }

    @Override
    public void modify(BoardDTO boardDTO) {

        Optional<Board> result = boardRepository.findById(boardDTO.getBno());

        Board board = result.orElseThrow();

        board.change(boardDTO.getTitle(), boardDTO.getContent());

        //첨부파일의 처리 645 추가
        board.clearImages();

        if(boardDTO.getFileNames() != null){
            for (String fileName : boardDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }


        boardRepository.save(board);

    }
    @Override
    public void remove(Long bno) {

        boardRepository.deleteById(bno);

    }


    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {

        // PageRequestDTO에서 검색 조건 및 페이지 정보를 추출합니다.
        String[] types = pageRequestDTO.getTypes(); // 검색 조건
        String keyword = pageRequestDTO.getKeyword(); // 검색어
        Pageable pageable = pageRequestDTO.getPageable("bno"); // 페이지 정보

        // 검색 조건과 페이지 정보를 사용하여 게시물을 조회합니다.
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        // 조회된 게시물을 BoardDTO로 변환합니다.
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        // PageResponseDTO를 생성하여 조회된 결과를 담아 반환합니다.
        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO) // 요청된 페이지 정보를 설정합니다.
                .dtoList(dtoList) // 조회된 게시물 목록을 설정합니다.
                .total((int) result.getTotalElements()) // 전체 게시물 수를 설정합니다.
                .build();
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {

        // 페이지 요청 DTO에서 검색 조건과 페이징 정보를 추출합니다.
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        // 게시글과 댓글 수를 포함하는 DTO의 페이지를 검색합니다.
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        // 결과를 PageResponseDTO 객체로 변환하여 반환합니다.
        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listwithall(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }


}
