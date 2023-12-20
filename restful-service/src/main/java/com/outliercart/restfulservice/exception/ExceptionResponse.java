package com.outliercart.restfulservice.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class ExceptionResponse {

    private Date timestamp;

    private String message;

    private String currentRequestPath;

    public ExceptionResponse() {
    }

    public ExceptionResponse(Date timestamp, String message, String currentRequestPath) {
        this.timestamp = timestamp;
        this.message = message;
        this.currentRequestPath = currentRequestPath;
    }
}
