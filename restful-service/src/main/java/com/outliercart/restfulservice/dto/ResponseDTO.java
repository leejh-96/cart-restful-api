package com.outliercart.restfulservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ResponseDTO {

    private String information;

    public ResponseDTO(String information) {
        this.information = information;
    }
}
