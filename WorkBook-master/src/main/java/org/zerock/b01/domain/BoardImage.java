package org.zerock.b01.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")
public class BoardImage implements Comparable<BoardImage>{

    @Id
    private String uuid;

    private String fileName;

    @ManyToOne
    private Board board;

    private int ord;

    @Override
    public int compareTo(BoardImage other) {
        return this.ord - other.ord;
    }

    public void changeBoard(Board board){  // board 객체 삭제시 참조 변경용
        this.board = board;
    }
}
