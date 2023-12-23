package com.outliercart.restfulservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@ToString
@Getter@Setter
public class CartItemsDTO extends RepresentationModel<CartItemsDTO> {

    private int cartNum;//장바구니 게시물 번호

    private int cartNo;//장바구니 생성 번호

    private int userNo;//유저 번호

    private int productNo;//상품 번호

    private int productQuantity;//장바구니에 담긴 상품 수량

    private String productName;//상품 이름

    private int productPrice;//상품 가격

    private int totalPrice;//상품 총 가격

    private String productContent;//상품 설명

}
