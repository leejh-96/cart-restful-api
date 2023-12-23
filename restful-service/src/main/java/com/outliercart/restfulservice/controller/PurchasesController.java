package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.*;
import com.outliercart.restfulservice.exception.UserNotFoundException;
import com.outliercart.restfulservice.service.CreateLinkService;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.PurchasesService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
public class PurchasesController {

    private final CreateLinkService createLinkService;

    private final LoginService loginService;

    private final PurchasesService purchasesService;

    public PurchasesController(CreateLinkService createLinkService, LoginService loginService, PurchasesService purchasesService) {
        this.createLinkService = createLinkService;
        this.loginService = loginService;
        this.purchasesService = purchasesService;
    }

    @PostMapping("/purchases-items")
    public ResponseEntity<EntityModel<ResponseDTO>> createdPurchases(@RequestBody PurchaseItemDTO purchaseItemDTO, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        if (!userNo.equals(purchaseItemDTO.getUserNo()))
            throw new UserNotFoundException("일치하지 않은 사용자입니다.");

        purchaseItemDTO = purchasesService.createdPurchases(purchaseItemDTO);

        // 생성된 Purchase 리소스 번호를 반환
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("purchaseNo : "+ purchaseItemDTO.getPurchasesItemsNo()));

        // 구매 목록 리스트 링크 생성
        Link allPurchasesListLink = linkTo(methodOn(this.getClass()).allPurchasesItems(null,null)).withRel("All-Purchases-List");
        // 구매 상세 정보 보기 링크 생성
        Link selectedPurchasesLink = linkTo(methodOn(this.getClass()).selectedPurchases(purchaseItemDTO.getPurchasesItemsNo(), null,null)).withRel("Selected-Purchases");
        // 상품 목록 리스트 및 검색 링크 생성
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        // 장바구니 목록 리스트 링크 생성
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");

        entityModel.add(allPurchasesListLink);
        entityModel.add(selectedPurchasesLink);
        entityModel.add(allProductsListLink);
        entityModel.add(allCartsListLink);

        // 201 CREATED HTTP Status Code 와 HTTP Body 생성된 구매 번호와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @GetMapping("/purchases-items")
    public ResponseEntity<CollectionModel<PurchasesDTO>> allPurchasesItems(@ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        pageInfo.setCount(purchasesService.allPurchasesCount(userNo));
        pageInfo.pageSettings();

        List<PurchasesDTO> purchasesList = purchasesService.allPurchasesList(pageInfo, userNo);

        for (PurchasesDTO dto : purchasesList){
            // 구매 목록 상세 보기 경로 변수와 파라미터 세팅
            UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest()
                                                                    .path("/{purchasesItemsNo}")
                                                                    .replaceQueryParam("page", pageInfo.getPage())
                                                                    .buildAndExpand(dto.getPurchasesItemsNo());
            // 구매 목록 상세 보기 링크 생성 후 DTO에 담기
            dto.add(Link.of(uriComponents.toUriString(), "Selected-Purchases-Item"));
        }

        CollectionModel<PurchasesDTO> collectionModel = CollectionModel.of(purchasesList);

        // 구매 목록 페이징 처리를 위한 쿼리 파라미터 세팅
        List<Link> paginationLinks = createLinkService.createPaginationLinks(this.getClass(), pageInfo);
        // 장바구니 구매하기 링크 생성
        Link createPurchaseLink = linkTo(methodOn(this.getClass()).createdPurchases(null,null)).withRel("Create-Purchase");
        // 상품 목록 리스트 및 검색 링크 생성
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        // 장바구니 목록 리스트 링크 생성
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");

        for (Link link : paginationLinks){
            // 생성된 페이징 링크를 collectionModel에 순서대로 추가
            collectionModel.add(link);
        }
        collectionModel.add(allProductsListLink);
        collectionModel.add(allCartsListLink);
        collectionModel.add(createPurchaseLink);

        // 200 OK HTTP Status Code 와 HTTP Body 구매 목록 리스트와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @GetMapping("/purchases-items/{purchasesItemsNo}")
    public ResponseEntity<EntityModel<PurchasesDTO>> selectedPurchases(@PathVariable int purchasesItemsNo, @ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        pageInfo.prevPageSettings();
        PurchasesDTO selectedPurchaseItem = purchasesService.selectedPurchases(purchasesItemsNo,userNo);

        EntityModel<PurchasesDTO> entityModel = EntityModel.of(selectedPurchaseItem);

        // 이전으로 돌아가기 위해서 이전 구매 목록 리스트 경로 변수와 쿼리 파라미터 세팅
        UriComponentsBuilder productsList = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                    .path("/purchases-items")
                                                                    .replaceQueryParam("page", pageInfo.getPage());

        // 장바구니 구매 하기 링크 생성
        Link createPurchaseLink = linkTo(methodOn(this.getClass()).createdPurchases(null,null)).withRel("Create-Purchase");
        // 이전 구매 목록 리스트 링크 생성
        Link prevPurchaseListLink = Link.of(productsList.toUriString(), "Prev-By-Purchase-List");
        // 구매 록록 리스트 링크 생성
        Link allPurchasesListLink = linkTo(methodOn(this.getClass()).allPurchasesItems(null, null)).withRel("All-Purchases-List");
        // 상품 목록 리스트 및 검색 링크 생성
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        // 장바구니 목록 리스트 링크 생성
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");

        entityModel.add(createPurchaseLink);
        entityModel.add(prevPurchaseListLink);
        entityModel.add(allPurchasesListLink);
        entityModel.add(allProductsListLink);
        entityModel.add(allCartsListLink);

        // 200 OK HTTP Status Code 와 HTTP Body 구매 정보와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
