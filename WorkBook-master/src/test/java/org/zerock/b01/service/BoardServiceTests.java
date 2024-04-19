package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.BoardRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testRegister() {

        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("제목 예시")
                .content("내용 예시")
                .writer("user01")
                .build();

        Long bno = boardService.register(boardDTO);

        log.info("bno: " + bno);
    }

    @Test
    public void testModify() {

        //변경에 필요한 데이터만
        //변경에 필요한 데이터
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("101번 수정")
                .content("101번")
                .build();

        //첨부파일을 하나 추가
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));

        boardService.modify(boardDTO);
    }

    @Test
    public void testList() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);

    }

    @Test
    public void testSearchAll(){

        String[] types = {"t","c","w"}; // 타입 : 제목, 내용, 작성자

        String keyword = "1";   // 키워드 수 : 1개

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

    }

    @Test
    public void testRegisterWithImages(){

        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("File...Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_bbb.jpg"
                ));

        Long bno = boardService.register(boardDTO);

        log.info("bno: " + bno);

    }

    @Test
    public void testReadAll(){

        Long bno = 101L;

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        for (String fileName : boardDTO.getFileNames()){
            log.info(fileName);
        }
    }

    @Test
    public void testListWithAll() {  // 649 추가

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListAllDTO> responseDTO =
                boardService.listwithall(pageRequestDTO);

        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {
            log.info(boardListAllDTO.getBno() + ":" + boardListAllDTO.getTitle());

            if (boardListAllDTO.getBoardImages() != null) {
                for (BoardImageDTO boardImage : boardListAllDTO.getBoardImages()) {
                    log.info(boardImage);
                }
            }

            log.info("-------------------------------");
        });


    }

}
