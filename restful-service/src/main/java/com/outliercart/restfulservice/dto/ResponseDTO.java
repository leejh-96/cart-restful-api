package com.outliercart.restfulservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ResponseDTO {

    private String information;//리소르 생성 번호 및 반환 메세지

    public ResponseDTO(String information) {
        this.information = information;
    }
}
