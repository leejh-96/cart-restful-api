package com.outliercart.restfulservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProductQuantityException extends RuntimeException{

    public ProductQuantityException(String message) {
        super(message);
    }
}
