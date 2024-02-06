package com.outliercart.restfulservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsersRegisterException extends RuntimeException{

    public UsersRegisterException(String message) {
        super(message);
    }

}
