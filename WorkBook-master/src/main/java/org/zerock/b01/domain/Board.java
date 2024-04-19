package org.zerock.b01.domain;

import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Board extends BaseEntity{

    // 식별번호 / pk
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    // 게시글 제목
    @Column(length = 500, nullable = false) //컬럼의 길이와 null허용여부
    private String title;

    // 게시글 내용
    @Column(length = 2000, nullable = false)
    private String content;

    // 작성자
    @Column(length = 50, nullable = false)
    private String writer;

    // 제목, 내용 변경하는 메서드
    public void change(String title, String content){
        this.title = title;
        this.content = content;
    }

    @OneToMany(mappedBy = "board", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName){
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }
    public void clearImages() {  // 618 추가

        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();
    }



}
