package org.zerock.b01.domain;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "Reply", indexes = {@Index(name = "idx_reply_board_bno", columnList = "board_bno")}) // 537추가
// 쿼리조건으로 자주사용되는 칼럼에는 인덱스를 생성
// Hibernate:
//    create index idx_reply_board_bno
//       on reply (board_bno)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board") // 객체로 사용하게 (ToString은 연관관계시 제외)
//@ToString
public class Reply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY) //fk 설정(지연로딩 : 필요한 순간까지 연결하지 않는다.)
    // EAGER(즉시로딩 : 엔티티 로딩할 때 같이 로딩 -> 성능영향 미침)
    private Board board;

    private String replyText;

    private String replyer;

    public void changeText(String text){
        this.replyText = text;
    }  // 552 추가

    //Hibernate:
    //    create table reply (
    //        rno bigint not null auto_increment,
    //        moddate datetime(6),
    //        regdate datetime(6),
    //        reply_text varchar(255),
    //        replyer varchar(255),
    //        board_bno bigint,
    //        primary key (rno)
    //    ) engine=InnoDB
    //Hibernate:
    //    alter table if exists reply
    //       add constraint FKr1bmblqir7dalmh47ngwo7mcs
    //       foreign key (board_bno)
    //       references board (bno)

}