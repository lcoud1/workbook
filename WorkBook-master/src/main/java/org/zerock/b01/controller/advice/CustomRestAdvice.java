package org.zerock.b01.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {

    // 이 메소드는 Spring 프레임워크에서 발생하는 BindException을 처리합니다.
    // BindException은 클라이언트의 요청 데이터를 서버의 컨트롤러 메소드의 매개변수나
    // 폼 객체로 바인딩할 때 발생하는 예외입니다.
    @ExceptionHandler(org.springframework.validation.BindException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED) // 예외가 발생했을 때 반환되는 HTTP 응답 상태 코드를 지정합니다.
    public ResponseEntity<Map<String, String>> handleBindException(BindException e){

        // 예외를 로깅합니다.
        log.error(e);

        // 발생한 오류를 담을 Map을 생성합니다.
        Map<String, String> errorMap = new HashMap<>();

        // 만약 예외에 오류가 있다면 처리합니다.
        if (e.hasErrors()){

            // 발생한 예외로부터 BindingResult를 가져옵니다.
            BindingResult bindingResult = e.getBindingResult();

            // 각 필드 오류를 반복하면서 오류 맵에 필드 이름과 코드를 추가합니다.
            bindingResult.getFieldErrors().forEach(fieldError ->
            {errorMap.put(fieldError.getField(), fieldError.getCode());
            });
        }
        // HTTP 응답을 생성하여 오류 맵을 포함한 응답 본문과 HTTP 상태 코드 400(Bad Request)을 반환합니다.
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // 메서드가 지정된 예외를 처리한다는 뜻
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)  // 메소드가 처리하는 예외에 대한 HTTP 응답 상태 코드를 지정
    // 무결성 위반 예외를 처리하기 위한 메서드
    public ResponseEntity<Map<String, String>> handleFKException(Exception e){

        log.error(e);

        // 오류 응답 맵 생성
        Map<String, String> errorMap = new HashMap<>();

        // 현재 시간을 맵에 추가
        errorMap.put("time", ""+ System.currentTimeMillis());
        // 오류 메시지를 맵에 추가
        errorMap.put("msh", "constraint fails");

        // 오류 응답 맵을 http 응답 본문에 담아서 반환
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class })
    // 564 추가 : 존재하지 않는 댓글 을 삭제할 때 처리
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(Exception e) {

        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        // 현재 시간을 맵에 추가
        errorMap.put("time", ""+System.currentTimeMillis());
        // 오류 메시지를 맵에 추가
        errorMap.put("msg",  "No Such Element Exception");

        // 오류 응답 맵을 http 응답 본문에 담아서 반환
        return ResponseEntity.badRequest().body(errorMap);
    }



}
