package org.zerock.b01.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.QBoard;
import org.zerock.b01.domain.QReply;
import org.zerock.b01.dto.BoardImageDTO;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl(){
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {

        QBoard board = QBoard.board;  // Q도메인 객체 사용

        JPQLQuery<Board> query = from(board); // select ??? from board ~~~~
        // JPQL : 엔티티 객체를 대상으로 쿼리함
        // from : 쿼리 소스를 추가한다.
        // innerJoin, join, leftJoin, fullJoin, on : 조인 부분을 추가한다. 조인 메서드에서 첫 번째 인자는 조인 소스이고, 두 번째 인자는 대상(별칭)이다.
        // where : 쿼리 필터를 추가한다. 가변 인자나 AND/OR 메서드를 이용해 필터를 추가한다.
        // groupBy : 가변인자 형식의 인자를 기준으로 그룹을 추가한다.
        // having : Predicate 표현식을 이용해 "group by" 그룹핑의 필터를 추가한다.
        // orderBy : 정렬 표현식을 이용해서 정렬 순서를 지정한다. 숫자나 문자열에 대해서는 asc()나 desc()를 사용하고, OrderSpecifier에 접근하기 위해 다른 비교 표현식을 사용한다.
        // limit, offset, restrict : 결과의 페이징을 설정한다. limit은 최대 결과 개수, offset은 결과의 시작 행, restrict는 limit과 offset을 함께 정의한다

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // ( 객체 생성

        booleanBuilder.or(board.title.contains("11")); // 조건 1 title like ...

        booleanBuilder.or(board.content.contains("11")); // 조건 2 content like ....

        query.where(booleanBuilder);  // where 조건문 추가
        query.where(board.bno.gt(0L)); // 인덱싱을 사용하기 위해 bno > 0 로 검색 조건 추가


        //paging Querydsl().applyPagination기능은 실행 마지막 쿼리에 limit가 적용
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();
        //fetch() :  리스트로 결과를 반환하는 방법입니다. (만약에 데이터가 없으면 빈 리스트를 반환해줍니다.)
        //fetchOne() : 단건을 조회할 때 사용하는 방법인데요. (결과가 없을때는 null 을 반환하고 결과가 둘 이상일 경우에는 NonUniqueResultException을 던집니다.)
        //fetchFirst() : 처음의 한건을 쿼리해서 가져오고 싶을때 사용하고요.
        //fetchResults() : 해당 내용은 페이징을 위해 사용될 수 있습니다. 페이징을 위해서 total contents를 가져오고요.
        //fetchCount() : count 쿼리를 날릴 수 있다.

        long count = query.fetchCount();


        return null;

    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        // String[] types (제목t, 내용c, 작성자w)를 가지고 있는 문자열배열

        // Querydsl을 사용하기 위해 Q도메인 객체를 생성합니다.
        QBoard board = QBoard.board;
        // JPQLQuery 객체를 생성합니다.
        JPQLQuery<Board> query = from(board);

        // 검색 조건과 키워드가 모두 존재하는 경우
        if (types != null && types.length > 0 && keyword != null) {
            // BooleanBuilder를 생성하여 조건을 동적으로 추가합니다.
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            // 검색 조건에 따라 조건을 추가합니다.
            for (String type : types) {
                switch (type) {
                    case "t": // 제목에 대한 검색 조건
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c": // 내용에 대한 검색 조건
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w": // 작성자에 대한 검색 조건
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            } // end for
            // 검색 조건을 where 절에 추가합니다.
            query.where(booleanBuilder);
        } // end if

        // 게시물 식별 번호(bno)가 0보다 큰 경우에 대한 검색 조건을 추가합니다.
        query.where(board.bno.gt(0L));

        // 페이지 정보를 적용합니다.
        this.getQuerydsl().applyPagination(pageable, query);

        // 쿼리를 실행하여 결과를 리스트로 가져옵니다.
        List<Board> list = query.fetch();

        // 전체 결과의 개수를 가져옵니다.
        long count = query.fetchCount();

        // PageImpl 객체를 사용하여 Page<Board> 객체를 생성하여 반환합니다.
        // PageImpl은 Spring Data에서 제공하는 Page 인터페이스의 구현체입니다.
        return new PageImpl<>(list, pageable, count);
        // 데이터를 가져온뒤 List 를 PageImpl 로 변환하기
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        // 게시글과 댓글을 조인할 Q타입 엔티티 객체들을 생성합니다.
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        // JPAQueryFactory를 이용하여 JPQL 쿼리를 생성합니다.
        JPQLQuery<Board> query = from(board);

        // 게시글과 댓글을 left join하여 연결합니다.
        query.leftJoin(reply).on(reply.board.eq(board));

        // 게시글을 기준으로 그룹화합니다.
        query.groupBy(board);

        // 검색 조건이 주어졌을 경우에만 실행합니다.
        if ((types != null && types.length > 0) && keyword != null){

            // BooleanBuilder를 사용하여 다양한 검색 조건을 OR 연산자로 묶습니다.
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for (String type: types){
                switch (type){
                    // 각 검색 조건에 따라 해당하는 필드를 검색합니다.
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            } // for 끝

            // 생성된 검색 조건을 where 절에 추가합니다.
            query.where(booleanBuilder);
        }

        // 게시글 번호가 0보다 큰 것만 조회합니다.
        query.where(board.bno.gt(0L));

        // 게시글과 댓글 수를 포함하는 DTO를 조회하기 위한 JPQL 쿼리를 생성합니다.
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.bean(BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count().as("replyCount")
        ));

        // 페이징 및 정렬 정보를 적용합니다.
        this.getQuerydsl().applyPagination(pageable, dtoQuery);

        // 조회된 결과를 DTO 리스트로 변환합니다.
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();

        // 전체 개수를 조회합니다.
        long count = dtoQuery.fetchCount();

        // DTO 리스트와 페이징 정보를 사용하여 Page 객체를 생성하여 반환합니다.
        return new PageImpl<>(dtoList, pageable, count);

    }

    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board)); //left join

        if ((types != null && types.length > 0) && keyword != null) {  // 638 추가 (검색조건추가)

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for (String type : types) {

                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }//end for
            boardJPQLQuery.where(booleanBuilder);
        }

        boardJPQLQuery.groupBy(board);

        getQuerydsl().applyPagination(pageable, boardJPQLQuery); //paging

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Board board1 = (Board) tuple.get(board);
            long replyCount = tuple.get(1, Long.class);

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //BoardImage를 BoardImageDTO 처리할 부분 637 추가
            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setBoardImages(imageDTOS);

            return dto;
        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount();


        return new PageImpl<>(dtoList, pageable, totalCount);

    }


}
