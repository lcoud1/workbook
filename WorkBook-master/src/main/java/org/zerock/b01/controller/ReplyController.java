package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
// 의존성 주입 (초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해 줍니다.)
// 새로운 필드를 추가할 때 다시 생성자를 만들어서 관리해야하는 번거로움을 없애준다. (@Autowired를 사용하지 않고 의존성 주입)
// @RequiredArgsConstructor 어노테이션은 클래스에 선언된 final 변수들, 필드들을 매개변수로 하는 생성자를 자동으로 생성해주는 어노테이션입니다.
public class ReplyController {

    private final ReplyService replyService;
    // @RequiredArgsConstructor 생성자 자동 주입


    @Operation(summary = "댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> register(@Valid @RequestBody ReplyDTO replyDTO, BindingResult bindingResult)
            throws BindException {
        log.info(replyDTO);

        // @Valid @RequestBody ReplyDTO replyDTO -> 들어오는 json 데이터를 자바객체로 변환
        // @valid -> 유효성 검사 담당
        // BindingResult bindingResult -> 들어오는 매개 변수에 대한 유효성 검사 결과를 저장하는 객체
        // throws BindException -> 유효성 검사 중에 발생한 오류를 처리

        // 유효성 검사 결과에 오류가 있는지 확인합니다.
        if (bindingResult.hasErrors()) {
            // 오류가 있으면 BindException을 발생시킵니다.
            throw new BindException(bindingResult);
        }

        // 결과를 담을 Map을 생성합니다.
        Map<String, Long> resultMap = new HashMap<>();

        // ReplyDTO를 이용하여 댓글을 등록하고, 결과를 resultMap에 저장합니다.
        Long rno = replyService.register(replyDTO);
        resultMap.put("rno", rno);

        // resultMap을 반환합니다.
        return resultMap;
    }

    @Operation(summary = "Replies of Board")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno, PageRequestDTO pageRequestDTO){

        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);

        return responseDTO;
    }

    @Operation(summary = "Get 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno){

        ReplyDTO replyDTO = replyService.read(rno);

        return replyDTO;
    }

    @Operation(summary = "Delete 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") Long rno){

        replyService.remove(rno);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }

    @Operation(summary =  "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE )
    public Map<String,Long> remove( @PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO ){
        // rno를 받아 객체를 수정 후 rno를 전달
        replyDTO.setRno(rno); //번호를 일치시킴

        replyService.modify(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }

}
