package org.zerock.b01.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> {
    // DTO의 목록과 시작페이지, 끝페이지에 대한 처리
    private int page;
    private int size;
    private int total;

    //시작 페이지 번호
    private int start;
    //끝 페이지 번호
    private int end;

    //이전 페이지의 존재 여부
    private boolean prev;
    //다음 페이지의 존재 여부
    private boolean next;

    private List<E> dtoList; // <E> 엘리먼트로 모든 요소를 의미 

    // build() 메서드에서 사용할 파라미터를 가진 생성자를 생성한다.
    // 예를 들어 phone, name 을 가지는 builder 가 있다면 phone, name 을 파라미터로 가지는 생성자가 생성된다.
    // Builder 는 내부적으로 클래스이름+Builder 라는 static class 를 객체 클래스 내부에 생성한다.
    // static Builder class 내부에 Builder 메서드의 target 이 되는 파라미터들을 non-static, non-final 로 생성한다.
    // static builder 기본 생성자를 생성한다.
    // build(), setter 역할을 하는 fieldName() 등의 메서드를 생성한다.
    // 그리고 각 builder 를 구분하기 위해 사용한 builderMethodName 옵션은 내부적으로 builderMethodName 에 선언한 이름으로 Builder 객체를 반환해주는 생성자가 생성된다.
    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total){

        if(total <= 0){
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.end =   (int)(Math.ceil(this.page / 10.0 )) *  10;  //화면에서의 마지막 번호

        this.start = this.end - 9; // 화면에서의 시작번호

        int last =  (int)(Math.ceil((total/(double)size))); // 데이터의 개수를 계산한 마지막 페이지 번호

        this.end =  end > last ? last: end;

        this.prev = this.start > 1;

        this.next =  total > this.end * this.size;

    }
}
