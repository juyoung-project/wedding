package kr.or.wds.project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Custom Exception
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ExceptionResponse> handleException(final CustomException e) {

        return ResponseEntity
                .status(e.getExceptionType().getStatus().value())
                .body(new ExceptionResponse(e.getExceptionType()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleException(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation error");

        log.error("[GlobalExceptionHandler] handleException: {}", e.getMessage(), e);

        ExceptionType exceptionType = ExceptionType.BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(exceptionType);
        exceptionResponse.setMessage(errorMessage);
        return ResponseEntity.status(exceptionType.getStatus().value()).body(exceptionResponse);
    }

    //http request 요청에서 json to java 객체 변환 실패 ex) enum 값, json 문법, 타입 불일치 등
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ExceptionResponse> handleException(final HttpMessageNotReadableException e) {
        log.error("[GlobalExceptionHandler] handleException: {}", e.getMessage(), e);

        ExceptionType exceptionType = ExceptionType.BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(exceptionType);
        exceptionResponse.setMessage("JSON conversion error");

        return ResponseEntity.status(exceptionType.getStatus().value()).body(exceptionResponse);
    }

}
