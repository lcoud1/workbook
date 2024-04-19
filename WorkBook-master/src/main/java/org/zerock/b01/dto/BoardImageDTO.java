package org.zerock.b01.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardImageDTO { // 이미지 엔티티를 처리함
    private String uuid;

    private String fileName;

    private int ord;

}