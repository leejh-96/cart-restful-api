package com.outliercart.restfulservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter@Setter
public class PurchaseItemDTO {

    private int purchasesItemsNo;//구매 번호

    private int cartNo;//장바구니 번호

    private Long userNo;//유저 번호

    private int productNo;//상품번호

    private int cartQuantity;//장바구니에 담긴 상품 수량

    private int productPrice;//상품 가격

    private int productQuantity;//상품 현재 수량

    private LocalDateTime purchaseDate;//구매 날짜

}
