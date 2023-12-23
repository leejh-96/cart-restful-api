package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartItemsDTO;
import com.outliercart.restfulservice.dto.CartsDTO;
import com.outliercart.restfulservice.dto.ResponseDTO;
import com.outliercart.restfulservice.service.CartsService;
import com.outliercart.restfulservice.service.CreateLinkService;
import com.outliercart.restfulservice.service.LoginService;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
public class CartsController {

    private final CreateLinkService createLinkService;

    private final LoginService loginService;

    private final CartsService cartsService;

    public CartsController(CreateLinkService createLinkService, LoginService loginService, CartsService cartsService) {
        this.createLinkService = createLinkService;
        this.loginService = loginService;
        this.cartsService = cartsService;
    }

    @PostMapping("/carts")
    public ResponseEntity<EntityModel<ResponseDTO>> createCarts(@Validated @RequestBody CartsDTO cartsDTO, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        cartsDTO.setUserNo(userNo);
        CartsDTO carts = cartsService.createdCarts(cartsDTO);

        // 생성된 Cart 리소스 번호를 반환
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("CartNo : "+carts.getCartNo()));

        // 장바구니 목록 리스트 링크 생성
        Link allCartsListLink = linkTo(methodOn(this.getClass()).allCarts(null,null)).withRel("All-Carts-List");
        // 장바구니 상품 상세 보기 링크 생성
        Link selectedCartsLink = linkTo(methodOn(this.getClass()).selectedCarts(carts.getCartNo(), null,null)).withRel("Selected-Carts");

        entityModel.add(allCartsListLink);
        entityModel.add(selectedCartsLink);

        // 201 CREATED Status Code 와 HTTP Body 장바구니 생성 번호와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @GetMapping("/carts")
    public ResponseEntity<CollectionModel<CartItemsDTO>> allCarts(@ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        pageInfo.setCount(cartsService.allCartsCount(userNo));
        pageInfo.pageSettings();

        List<CartItemsDTO> cartsList = cartsService.allCartsList(pageInfo, userNo);

        for (CartItemsDTO dto : cartsList){
            // 장바구니에 담긴 상품 상세 보기 경로 변수와 파라미터 세팅
            UriComponents selectedCartsUri = ServletUriComponentsBuilder.fromCurrentRequest()
                                                                        .path("/{cartNo}")
                                                                        .replaceQueryParam("page", pageInfo.getPage())
                                                                        .buildAndExpand(dto.getCartNo());
            // 장바구니 선택 삭제 경로 변수와 파라미터 세팅
            UriComponents removeSelectedCartsUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                            .path("/carts/{cartNo}")
                                                                            .buildAndExpand(dto.getCartNo());
            // 장바구니 상세 보기 링크 생성 후 DTO에 담기
            dto.add(Link.of(selectedCartsUri.toUriString(), "Selected-Carts"));
            // 장바구니 선택 삭제 링크 생성 후 DTO에 담기
            dto.add(Link.of(removeSelectedCartsUri.toUriString(), "Remove-Selected-Carts"));
        }

        CollectionModel<CartItemsDTO> collectionModel = CollectionModel.of(cartsList);

        // 장바구니 목록 페이징 처리를 위한 쿼리 파라미터 세팅
        List<Link> paginationLinks = createLinkService.createPaginationLinks(this.getClass(), pageInfo);

        // 장바구니 담기 링크 생성
        Link createCartsLink = linkTo(methodOn(this.getClass()).createCarts(null, null)).withRel("Create-Carts");
        // 장바구니 전체 삭제 링크 생성
        Link removeAllCarts = linkTo(methodOn(this.getClass()).updateAllCartItemStatus(null)).withRel("Remove-All-Carts");
        // 구매 목록 리스트 링크 생성
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");

        for (Link link : paginationLinks){
            // 생성된 페이징 링크를 collectionModel에 순서대로 추가
            collectionModel.add(link);
        }
        collectionModel.add(createCartsLink);
        collectionModel.add(removeAllCarts);
        collectionModel.add(allPurchaseListLink);

        // 200 OK HTTP Status Code 와 HTTP Body 장바구니 목록 리스트와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @GetMapping("/carts/{cartNo}")
    public ResponseEntity<EntityModel<CartItemsDTO>> selectedCarts(@PathVariable int cartNo, @ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        pageInfo.prevPageSettings();

        CartItemsDTO selectedCart = cartsService.selectedCarts(cartNo,userNo);

        EntityModel<CartItemsDTO> entityModel = EntityModel.of(selectedCart);

        // 이전으로 돌아가기 위해서 이전 장바구니 목록 리스트 경로 변수와 쿼리 파라미터 세팅
        UriComponentsBuilder cartsList = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                    .path("/carts")
                                                                    .replaceQueryParam("page", pageInfo.getPage());

        // 이전 장바구니 목록 리스트 링크 생성
        Link prevCartListLink = Link.of(cartsList.toUriString(), "Prev-By-Cart-List");
        // 장바구니 담기 링크 생성
        Link createCartLink = linkTo(methodOn(CartsController.class).createCarts(null, null)).withRel("Create-Carts");
        // 장바구니 선택 삭제 링크 생성
        Link removeSelectedCartLink = linkTo(methodOn(CartsController.class).updateCartItemStatus(selectedCart.getCartNo(), null)).withRel("Remove-Selected-Carts");
        // 장바구니 구매 링크 생성
        Link createPurchaseLink = linkTo(methodOn(PurchasesController.class).createdPurchases(null, null)).withRel("Create-Purchase");
        // 구매 목록 리스트 링크 생성
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");

        entityModel.add(prevCartListLink);
        entityModel.add(createCartLink);
        entityModel.add(removeSelectedCartLink);
        entityModel.add(createPurchaseLink);
        entityModel.add(allPurchaseListLink);

        // 200 OK HTTP Status Code 와 HTTP Body 장바구니 상품 상세 정보와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @PatchMapping("/carts/{cartNo}")
    public ResponseEntity updateCartItemStatus(@PathVariable int cartNo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        cartsService.updateCartItemStatus(userNo,cartNo);

        // 204 NO CONTENT HTTP Status Code 반환
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/carts")
    public ResponseEntity updateAllCartItemStatus(HttpSession session){

        Long userNo = loginService.userLoginCheck(session);
        cartsService.updateAllCartItemStatus(userNo);

        // 204 NO CONTENT HTTP Status Code 반환
        return ResponseEntity.noContent().build();
    }

}
