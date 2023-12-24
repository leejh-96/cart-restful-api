package com.outliercart.restfulservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter@Setter
public class CartsDTO {

    private int count;//총 카트 목록 갯수

    private int cartNo;//카트 번호

    private Long userNo;//유저 번호

    @Min(value = 1, message = "상품 번호는 최소 1번부터 입니다.")
    private Integer productNo;//상품 번호

    @Min(value = 1, message = "상품 수량은 최소 1개 이상 입니다.")
    private Integer productQuantity;//상품 수량

}
