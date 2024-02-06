package com.outliercart.restfulservice.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

@ToString
@Getter@Setter
public class ProductsDTO extends RepresentationModel<ProductsDTO> {

    private int productNum;//상품 게시물 번호

    private int productNo;//상품 생성 번호

    @NotBlank(message = "상품 이름은 필수입니다.")
    @Length(max = 33, message = "상품 이름은 최대 33자까지 허용됩니다.")
    private String productName;//상품 이름

    @Max(value = 100000, message = "상품 가격은 최대 100,000원까지만 허용됩니다.")
    @Min(value = 1000, message = "상품 가격은 최소 1,000원입니다.")
    private Integer productPrice;//상품 가격

    @Max(value = 1000, message = "상품 수량은 최대 1,000개까지만 허용됩니다.")
    @Min(value = 10, message = "상품 수량은 최소 10개입니다.")
    private Integer productQuantity;//상품 수량

    @NotBlank(message = "상품 설명은 필수입니다.")
    @Length(max = 100, message = "상품 설명은 최대 100자까지 허용됩니다.")
    private String productContent;//상품 설명

}
