package com.outliercart.restfulservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@ToString
@Getter@Setter
public class PurchasesDTO extends RepresentationModel<PurchasesDTO> {

    private int postsNum;//구매 리스트 번호

    private int purchasesItemsNo;//구매 번호

    private int productNo;//상품번호

    private String productName;//상품 이름

    private String productContent;//상품 설명

    private int quantity;//주문한 상품 수량

    private int productPrice;//상품 가격

    private int totalPrice;//총 상품 가격

    private LocalDateTime purchaseDate;//구매 날짜

}
