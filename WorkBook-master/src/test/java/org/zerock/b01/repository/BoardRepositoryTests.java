package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.BoardImage;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert() {
        // 1부터 100까지의 숫자 범위를 생성하고 각각의 숫자에 대해 반복 작업을 수행합니다.
        IntStream.rangeClosed(1, 100).forEach(i -> {
            // 새로운 게시물을 생성합니다.
            Board board = Board.builder()
                    .title("title..." + i) // 제목을 설정합니다.
                    .content("content..." + i) // 내용을 설정합니다.
                    .writer("user" + (i % 10)) // 작성자를 설정합니다. 작성자는 user1부터 user10까지 순환됩니다.
                    .build();

            // 생성된 게시물을 저장하고, 결과로 반환된 엔티티를 result 변수에 저장합니다.
            Board result = boardRepository.save(board);

            // 저장된 게시물의 식별 번호(BNO)를 로그에 출력합니다.
            log.info("BNO: " + result.getBno());
        });
    }


    @Test
    public void testSelect() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);
        // findById()을 리턴 타입은 Optional 참조하더라도 NPE가 발생하지 않도록 도와준다.
        // Optional 클래스는 아래와 같은 value에 값을 저장하기 때문에 값이 null이더라도 바로 NPE가 발생하지 않으며, 클래스이기 때문에 각종 메소드를 제공해준다.
        // NPE : 널포인트익셉션
        // Optional.empty() -> 널값인 경우,  Optional.of() -> 널값이 아닌 경우, Optional.ofNullbale() - 값이 Null일수도, 아닐수도 있는 경우

        Board board = result.orElseThrow();
        // Optional 객체의 유무를 판단하고 예외를 처리하기 위해 if문을 사용해왔습니다.
        // if문을 사용하면서 예외 처리 또는 값을 반환하다보니 코드의 가독성이 떨어졌습니다.
        // orElseThrow를 통해 Optional에서 원하는 객체를 바로 얻거나 예외를 던질 수 있습니다.

        log.info(board);

    }

    @Test
    public void testUpdate() {
        // 업데이트할 게시물의 식별 번호
        Long bno = 100L;

        // 게시물 식별 번호로 해당 게시물을 조회합니다.
        Optional<Board> result = boardRepository.findById(bno);

        // Optional에서 게시물 객체를 추출합니다. 만약 조회 결과가 없으면 NoSuchElementException이 발생합니다.
        // 따라서 orElseThrow() 메서드를 사용하여 예외가 발생하도록 합니다.
        Board board = result.orElseThrow(); // -> new NoSuchElementException("게시물이 존재하지 않습니다."));

        // 게시물의 제목과 내용을 변경합니다.
        // "update..title 100"는 변경된 제목을 나타내며, "update content 100"은 변경된 내용을 나타냅니다.
        board.change("update..title 100", "update content 100");

        // 변경된 게시물을 저장합니다.
        // JPA의 save 메서드를 사용하여 변경된 게시물을 영속화합니다.
        // 이는 JPA의 영속성 컨텍스트에 엔티티를 저장하고, 트랜잭션이 커밋될 때 DB에 실제로 반영됩니다.
        boardRepository.save(board);
    }



    @Test
    public void testDelete() {
        Long bno = 1L;

        boardRepository.deleteById(bno);
    }

    @Test
    public void testPaging() {
        // 페이지네이션 처리를 위한 Pageable 객체를 생성합니다.
        // 첫 번째 페이지를 가져오며 페이지당 10개의 결과를 가져옵니다.
        // 게시물 식별 번호(bno)를 기준으로 내림차순으로 정렬합니다.
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        // 페이징 처리된 결과를 조회합니다.
        Page<Board> result = boardRepository.findAll(pageable);

        // 전체 게시물 수를 로그에 출력합니다.
        log.info("총 게시물 수: " + result.getTotalElements());

        // 전체 페이지 수를 로그에 출력합니다.
        log.info("총 페이지 수: " + result.getTotalPages());

        // 현재 페이지 번호를 로그에 출력합니다.
        log.info("현재 페이지 번호: " + result.getNumber());

        // 페이지당 결과 수를 로그에 출력합니다.
        log.info("페이지 크기: " + result.getSize());

        // 현재 페이지에 있는 게시물 목록을 가져옵니다.
        List<Board> boardList = result.getContent();

        // 각 게시물을 로그에 출력합니다.
        boardList.forEach(board -> log.info(board.toString()));
    }

    @Test
    public void testSearch1() {
        // 페이지네이션 처리를 위한 Pageable 객체를 생성합니다.
        // 두 번째 페이지를 가져오며 페이지당 10개의 결과를 가져옵니다.
        // 게시물 식별 번호(bno)를 기준으로 내림차순으로 정렬합니다.
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());

        // 검색 메서드를 호출하여 결과를 조회합니다.
        // 이 메서드는 JpaRepository 인터페이스에 정의되지 않은 사용자 정의 메서드일 것으로 예상됩니다.
        // 그러므로 해당 메서드가 무엇을 수행하는지에 대한 정보가 주석으로 제공되어야 합니다.
        boardRepository.search1(pageable);
    }


    @Test
    public void testSearchAll() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable );

    }

    @Test
    public void testSearchAll2() {

        // 타입 종류
        String[] types = {"t","c","w"};

        // 키워드 개수
        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable );

        // 전체 페이지
        log.info(result.getTotalPages());

        // 페이지 사이즈
        log.info(result.getSize());

        // 페이지 갯수
        log.info(result.getNumber());

        // 이전, 다음 페이지
        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testSearchReplyCount(){

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        // 전체 페이지
        log.info(result.getTotalPages());
        // 페이지 크기
        log.info(result.getSize());
        //
        log.info(result.getNumber());
        //prev next
        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(board -> log.info(board));

    }

    @Test
    public void testInsertWithImages() { // 619 테스트

        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();

        for (int i = 0; i < 3; i++) {  //3개의 파일 저장

            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");

        }
        boardRepository.save(board);
    }

    @Transactional
    @Test
    public void testReadWithImages(){

        Optional<Board> result = boardRepository.findById(1L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("------------");
        for(BoardImage boardImage : board.getImageSet()){
            log.info(boardImage);
        }

    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages() {
        // 게시물 첨부파일 수정 테스트 : 실제 삭제가 안됨, orphanRemoval = true 추가(고아 객체 삭제용)
        //부모 엔티티와 연관관계가 끊어진 자식 엔티티를 가리킵니다.
        //부모가 제거될때, 부모와 연관되어있는 모든 자식 엔티티들은 고아객체가 됩니다.
        //부모 엔티티와 자식 엔티티 사이의 연관관계를 삭제할때, 해당 자식 엔티티는 고아객체가 됩니다.

        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        //기존의 첨부파일들은 삭제
        board.clearImages();

        //새로운 첨부파일들
        for (int i = 0; i < 2; i++) {

            board.addImage(UUID.randomUUID().toString(), "updatefile"+i+".jpg");
        }

        boardRepository.save(board);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {  // 1번 개시물 삭제시 댓글도 삭제 627

        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);

    }

    @Test
    public void testInsertAll() { // 627 100게시물, 3개의 파일 추가, 5의 배수는 첨부 없음

        for (int i = 1; i <= 100; i++) {

            Board board  = Board.builder()
                    .title("Title.."+i)
                    .content("Content.." + i)
                    .writer("writer.." + i)
                    .build();

            for (int j = 0; j < 3; j++) {

                if(i % 5 == 0){
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
            }
            boardRepository.save(board);

        }//end for
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount(){

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);

        log.info("-----------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));

    }




}
