package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.ProductsDTO;
import com.outliercart.restfulservice.dto.ResponseDTO;
import com.outliercart.restfulservice.service.CreateLinkService;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
public class ProductsController {

    private final CreateLinkService createLinkService;

    private final LoginService loginService;

    private final ProductService productService;

    public ProductsController(CreateLinkService createLinkService, LoginService loginService, ProductService productService) {
        this.createLinkService = createLinkService;
        this.loginService = loginService;
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<EntityModel<ResponseDTO>> createProducts(@Validated @RequestBody ProductsDTO productsDTO,
                                                              HttpSession session){

        loginService.userLoginCheck(session);

        ProductsDTO productDTO = productService.createdProducts(productsDTO);

        // 생성된 Product 리소스 번호를 반환
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("ProductNo : "+productDTO.getProductNo()));

        // 상품 목록 리스트 및 검색 링크 생성
        Link allProductsListLink = linkTo(methodOn(this.getClass()).allProducts(null)).withRel("All-Products-List");
        // 상품 상세 보기 링크 생성
        Link selectedProductsLink = linkTo(methodOn(this.getClass()).selectedProducts(productDTO.getProductNo(), null)).withRel("Selected-Products");

        entityModel.add(allProductsListLink);
        entityModel.add(selectedProductsLink);

        // 201 CREATED HTTP Status Code 와 HTTP Body 생성된 상품 번호와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @GetMapping("/products")
    public ResponseEntity<CollectionModel<ProductsDTO>> allProducts(@ModelAttribute PageInfo pageInfo){

        pageInfo.setCount(productService.allProductsCount(pageInfo));
        pageInfo.pageSettings();

        List<ProductsDTO> productsList = productService.allProductsList(pageInfo);

        // 상품 상세 보기 링크를 생성해 ProductDTO 객체에 추가
        for (ProductsDTO dto : productsList){
            UriComponents selectedProductUri = ServletUriComponentsBuilder.fromCurrentRequest()
                                                                        .path("/{productNo}")
                                                                        .replaceQueryParam("searchType", pageInfo.getSearchType())
                                                                        .replaceQueryParam("searchContent", pageInfo.getSearchContent())
                                                                        .replaceQueryParam("page", pageInfo.getPage())
                                                                        .buildAndExpand(dto.getProductNo());
            // 상품 상세 보기 링크 생성 후 DTO에 담기
            dto.add(Link.of(selectedProductUri.toUriString()).withRel("Selected-Products"));
        }

        CollectionModel<ProductsDTO> collectionModel = CollectionModel.of(productsList);

        // 상품 목록 페이징 처리를 위한 쿼리 파라미터 세팅(CreateLinkService의 createPaginationLinks 메서드를 호출해 링크 생성)
        List<Link> paginationLinks = createLinkService.createPaginationLinks(this.getClass(), pageInfo);
        // 상품 목록 추가 링크 생성
        Link createProductsLink = linkTo(methodOn(this.getClass()).createProducts(null, null)).withRel("Create-Products");
        // 장바구니 목록 리스트 링크 생성
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null, null)).withRel("All-Carts-List");
        // 구매 목록 리스트 링크 생성
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");

        for (Link link : paginationLinks){
            // 생성된 페이징 링크를 collectionModel에 순서대로 추가
            collectionModel.add(link);
        }
        collectionModel.add(createProductsLink);
        collectionModel.add(allCartsListLink);
        collectionModel.add(allPurchaseListLink);

        // 200 OK HTTP Status Code 와 HTTP Body 상품 목록 검색 리스트와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @GetMapping("/products/{productNo}")
    public ResponseEntity<EntityModel<ProductsDTO>> selectedProducts(@PathVariable int productNo, @ModelAttribute PageInfo pageInfo){

        pageInfo.prevPageSettings();
        ProductsDTO selectedProduct = productService.selectedProducts(productNo);

        EntityModel<ProductsDTO> entityModel = EntityModel.of(selectedProduct);

        // 이전으로 돌아가기 위한 이전 페이지 정보 쿼리 파라미터 세팅
        UriComponentsBuilder productsList = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                    .path("/products")
                                                                    .replaceQueryParam("searchType", pageInfo.getSearchType())
                                                                    .replaceQueryParam("searchContent", pageInfo.getSearchContent())
                                                                    .replaceQueryParam("page", pageInfo.getPage());

        // 이전 페이지 정보 링크 생성
        Link prevProductListLink = Link.of(productsList.toUriString(), "Prev-By-Product-List");
        // 장바구니 담기 링크 생성
        Link createCartLink = linkTo(methodOn(CartsController.class).createCarts(null, null)).withRel("Create-Carts");

        entityModel.add(prevProductListLink);
        entityModel.add(createCartLink);

        // 200 OK HTTP Status Code 와 HTTP Body 상품 상세 정보와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
