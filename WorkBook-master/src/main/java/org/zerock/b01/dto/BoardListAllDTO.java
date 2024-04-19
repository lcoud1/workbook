package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardListAllDTO { /// Board와 이미지, 댓글 개수를 모두 반영할 수 있는 DTO

    private Long bno;

    private String title;

    private String writer;

    private LocalDateTime regDate;

    private Long replyCount;


    private List<BoardImageDTO> boardImages;

}
