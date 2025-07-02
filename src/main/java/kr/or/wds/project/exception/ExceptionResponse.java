package kr.or.wds.project.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ExceptionResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String code;
    @Setter
    private String message;

    public ExceptionResponse(ExceptionType exceptionType) {
        this.timestamp = LocalDateTime.now();
        this.status = exceptionType.getStatus().value();
        this.error = exceptionType.getStatus().name();
        this.code = exceptionType.name();
        this.message = exceptionType.getMessage();
    }

}

