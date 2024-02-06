package com.outliercart.restfulservice.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class ExceptionResponse {

    private Date timestamp; // 예외 발생 시간

    private String message; // 예외 메세지

    private String currentRequestPath; // 예외가 발생한 요청 경로

    public ExceptionResponse() {
    }

    public ExceptionResponse(Date timestamp, String message, String currentRequestPath) {
        this.timestamp = timestamp;
        this.message = message;
        this.currentRequestPath = currentRequestPath;
    }
}
