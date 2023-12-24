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

    /**
     * 새로운 구매 생성 및 관련 링크 제공
     *
     * @param purchaseItemDTO 생성할 구매 정보
     * @param session 현재 세션 정보
     * @link 구래 목록 리스트, 구매 상세보기, 상품 목록 리스트 및 검색, 장바구니 목록 리스트
     * @return 201 CREATED HTTP Status Code 와 HTTP Body 생성된 구매 번호와 링크를 담아서 반환
     */
    @PostMapping("/purchases-items")
    public ResponseEntity<EntityModel<ResponseDTO>> createdPurchases(@RequestBody PurchaseItemDTO purchaseItemDTO, HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 요청한 사용자 번호와 로그인된 사용자 번호가 일치하지 않으면 사용자를 찾을 수 없는 예외를 발생시킵니다. */
        if (!userNo.equals(purchaseItemDTO.getUserNo()))
            throw new UserNotFoundException("일치하지 않은 사용자입니다.");

        /* 구매 생성 */
        purchaseItemDTO = purchasesService.createdPurchases(purchaseItemDTO);

        /* 생성된 구매 번호를 ResponseDTO(응답 객체)에 담아 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("purchaseNo : "+ purchaseItemDTO.getPurchasesItemsNo()));

        Link allPurchasesListLink = linkTo(methodOn(this.getClass()).allPurchasesItems(null,null)).withRel("All-Purchases-List");
        Link selectedPurchasesLink = linkTo(methodOn(this.getClass()).selectedPurchases(purchaseItemDTO.getPurchasesItemsNo(), null,null)).withRel("Selected-Purchases");
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");

        entityModel.add(allPurchasesListLink);
        entityModel.add(selectedPurchasesLink);
        entityModel.add(allProductsListLink);
        entityModel.add(allCartsListLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    /**
     * 모든 구매 항목 조회 및 관련 링크 제공
     *
     * @param pageInfo 페이지 정보
     * @param session 현재 세션 정보
     * @link 구매 목록 상세 보기, 구매 목록 리스트 및 페이징 처리, 상품 구매, 상품 목록 리스트 및 검색, 장바구니 목록 리스트
     * @return 200 OK HTTP Status Code 와 HTTP Body 구매 목록 리스트와 링크를 담아서 반환
     */
    @GetMapping("/purchases-items")
    public ResponseEntity<CollectionModel<PurchasesDTO>> allPurchasesItems(@ModelAttribute PageInfo pageInfo, HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 현재 요청에 대한 페이징 처리 */
        pageInfo.setCount(purchasesService.allPurchasesCount(userNo));
        pageInfo.pageSettings();

        /* 데이터베이스에서 구매 목록 반환 */
        List<PurchasesDTO> purchasesList = purchasesService.allPurchasesList(pageInfo, userNo);

        for (PurchasesDTO dto : purchasesList){
            /* 구매 목록 상세 보기 경로 변수와 파라미터 세팅 */
            UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest()
                                                                    .path("/{purchasesItemsNo}")
                                                                    .replaceQueryParam("page", pageInfo.getPage())
                                                                    .buildAndExpand(dto.getPurchasesItemsNo());
            /* 구매 목록 상세 보기 링크 생성 후 DTO에 담기 */
            dto.add(Link.of(uriComponents.toUriString(), "Selected-Purchases-Item"));
        }

        /* purchasesList 객체를 CollectionModel 변환하여 각 PurchasesDTO에 HATEOAS 링크를 포함시킴 */
        CollectionModel<PurchasesDTO> collectionModel = CollectionModel.of(purchasesList);

        Link createPurchaseLink = linkTo(methodOn(this.getClass()).createdPurchases(null,null)).withRel("Create-Purchase");
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");
        List<Link> paginationLinks = createLinkService.createPaginationLinks(this.getClass(), pageInfo);

        /* 생성된 페이징 링크를 collectionModel에 순서대로 추가 */
        for (Link link : paginationLinks){
            collectionModel.add(link);
        }
        collectionModel.add(allProductsListLink);
        collectionModel.add(allCartsListLink);
        collectionModel.add(createPurchaseLink);

        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    /**
     * 특정 구매 항목 조회 및 관련 링크 제공
     *
     * @param purchasesItemsNo 검색할 특정 구매 아이템 번호
     * @param pageInfo 페이지 정보
     * @param session 현재 세션 정보
     * @link 이전 구매 목록 리스트로 돌아가기, 상품 구매, 구매 목록 리스트, 상품 목록 리스트 및 검색, 장바구니 목록 리스트
     * @return 200 OK HTTP Status Code 와 HTTP Body 구매 정보와 링크를 담아서 반환
     */
    @GetMapping("/purchases-items/{purchasesItemsNo}")
    public ResponseEntity<EntityModel<PurchasesDTO>> selectedPurchases(@PathVariable int purchasesItemsNo, @ModelAttribute PageInfo pageInfo, HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 구매 목록으로 돌아가기 위해 이전 페이지 번호 세팅 */
        pageInfo.prevPageSettings();

        /* 데이터베이스에서 구매 항목 조회 정보 반환 */
        PurchasesDTO selectedPurchaseItem = purchasesService.selectedPurchases(purchasesItemsNo,userNo);

        /* selectedPurchaseItem 객체를 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<PurchasesDTO> entityModel = EntityModel.of(selectedPurchaseItem);

        /* 이전으로 돌아가기 위해서 이전 구매 목록 리스트 경로 변수와 쿼리 파라미터 세팅 */
        UriComponentsBuilder productsList = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                    .path("/purchases-items")
                                                                    .replaceQueryParam("page", pageInfo.getPage());

        Link createPurchaseLink = linkTo(methodOn(this.getClass()).createdPurchases(null,null)).withRel("Create-Purchase");
        Link prevPurchaseListLink = Link.of(productsList.toUriString(), "Prev-By-Purchase-List");
        Link allPurchasesListLink = linkTo(methodOn(this.getClass()).allPurchasesItems(null, null)).withRel("All-Purchases-List");
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");

        entityModel.add(createPurchaseLink);
        entityModel.add(prevPurchaseListLink);
        entityModel.add(allPurchasesListLink);
        entityModel.add(allProductsListLink);
        entityModel.add(allCartsListLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
