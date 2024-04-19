package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.Board;
import org.zerock.b01.repository.search.BoardSearch;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    @EntityGraph(attributePaths = {"imageSet"}) // attributePaths 같이 로딩해야 하는 속성을 명시함.
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithImages(Long bno); // bno를 이용해 이미지를 찾아옴.


}
