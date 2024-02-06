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

    /**
     * 새로운 카트 생성 및 사용자에게 링크 반환
     *
     * @param cartsDTO 새로운 카트 정보
     * @param session 현재 세션 정보
     * @link 장바구니 목록 리스트, 장바구니 상품 상세 보기
     * @return 201 CREATED Status Code 와 HTTP Body 장바구니 생성 번호와 링크를 담아서 반환
     */
    @PostMapping("/carts")
    public ResponseEntity<EntityModel<ResponseDTO>> createCarts(@Validated @RequestBody CartsDTO cartsDTO, HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 카트 생성 */
        cartsDTO.setUserNo(userNo);
        CartsDTO carts = cartsService.createdCarts(cartsDTO);

        /* 생성된 카트 번호를 ResponseDTO(응답 객체)에 담아 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("CartNo : "+carts.getCartNo()));

        Link allCartsListLink = linkTo(methodOn(this.getClass()).allCarts(null,null)).withRel("All-Carts-List");
        Link selectedCartsLink = linkTo(methodOn(this.getClass()).selectedCarts(carts.getCartNo(), null,null)).withRel("Selected-Carts");

        entityModel.add(allCartsListLink);
        entityModel.add(selectedCartsLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    /**
     * 모든 카트 항목 조회 및 관련 링크 제공
     *
     * @param pageInfo 페이지 정보
     * @param session 현재 세션 정보
     * @link 장바구니 상세 보기, 장바구니 선택 삭제, 장바구니 전체 삭제, 장바구니 담기, 구매 목록 리스트, 페이징 처리 링크
     * @return 200 OK HTTP Status Code 와 HTTP Body 장바구니 목록 리스트와 링크를 담아서 반환
     */
    @GetMapping("/carts")
    public ResponseEntity<CollectionModel<CartItemsDTO>> allCarts(@ModelAttribute PageInfo pageInfo, HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 현재 요청에 대한 페이징 처리 */
        pageInfo.setCount(cartsService.allCartsCount(userNo));
        pageInfo.pageSettings();

        /* 데이터베이스에서 카트 목록 반환 */
        List<CartItemsDTO> cartsList = cartsService.allCartsList(pageInfo, userNo);

        for (CartItemsDTO dto : cartsList){
            /* 장바구니에 담긴 상품 상세 보기 경로 변수와 파라미터 세팅 */
            UriComponents selectedCartsUri = ServletUriComponentsBuilder.fromCurrentRequest()
                                                                        .path("/{cartNo}")
                                                                        .replaceQueryParam("page", pageInfo.getPage())
                                                                        .buildAndExpand(dto.getCartNo());
            /* 장바구니 선택 삭제 경로 변수와 파라미터 세팅 */
            UriComponents removeSelectedCartsUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                            .path("/carts/{cartNo}")
                                                                            .buildAndExpand(dto.getCartNo());
            /* 장바구니 상세 보기 링크 생성 후 DTO에 담기 */
            dto.add(Link.of(selectedCartsUri.toUriString(), "Selected-Carts"));

            /* 장바구니 선택 삭제 링크 생성 후 DTO에 담기 */
            dto.add(Link.of(removeSelectedCartsUri.toUriString(), "Remove-Selected-Carts"));
        }

        /* cartsList객체를 CollectionModel 변환하여 각 CartItemsDTO에 HATEOAS 링크를 포함시킴 */
        CollectionModel<CartItemsDTO> collectionModel = CollectionModel.of(cartsList);

        Link createCartsLink = linkTo(methodOn(this.getClass()).createCarts(null, null)).withRel("Create-Carts");
        Link removeAllCarts = linkTo(methodOn(this.getClass()).updateAllCartItemStatus(null)).withRel("Remove-All-Carts");
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");
        /* 장바구니 목록 페이징 처리를 위한 쿼리 파라미터 세팅 후 페이징 HATEOAS 링크 반환 */
        List<Link> paginationLinks = createLinkService.createPaginationLinks(this.getClass(), pageInfo);

        /* 생성된 페이징 링크를 collectionModel에 순서대로 추가 */
        for (Link link : paginationLinks){
            collectionModel.add(link);
        }
        collectionModel.add(createCartsLink);
        collectionModel.add(removeAllCarts);
        collectionModel.add(allPurchaseListLink);

        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    /**
     * 특정 카트 항목 조회 및 관련 링크 제공
     *
     * @param cartNo 조회할 카트 번호
     * @param pageInfo 페이지 정보
     * @param session 현재 세션 정보
     * @link 이전 장바구니 목록 리스트로 돌아가기, 장바구니 담기, 장바구니 선택 삭제, 상품 구매, 구매 목록 리스트
     * @return 200 OK HTTP Status Code 와 HTTP Body 장바구니 상품 상세 정보와 링크를 담아서 반환
     */
    @GetMapping("/carts/{cartNo}")
    public ResponseEntity<EntityModel<CartItemsDTO>> selectedCarts(@PathVariable int cartNo, @ModelAttribute PageInfo pageInfo, HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 카트 목록으로 돌아가기 위해 이전 페이지 번호 세팅 */
        pageInfo.prevPageSettings();

        /* 데이터베이스에서 카트 정보 반환 */
        CartItemsDTO selectedCart = cartsService.selectedCarts(cartNo,userNo);

        /* selectedCart 객체를 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<CartItemsDTO> entityModel = EntityModel.of(selectedCart);

        /* 이전으로 돌아가기 위해서 이전 장바구니 목록 리스트 경로 변수와 쿼리 파라미터 세팅 */
        UriComponentsBuilder cartsList = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                    .path("/carts")
                                                                    .replaceQueryParam("page", pageInfo.getPage());

        Link prevCartListLink = Link.of(cartsList.toUriString(), "Prev-By-Cart-List");
        Link createCartLink = linkTo(methodOn(CartsController.class).createCarts(null, null)).withRel("Create-Carts");
        Link removeSelectedCartLink = linkTo(methodOn(CartsController.class).updateCartItemStatus(selectedCart.getCartNo(), null)).withRel("Remove-Selected-Carts");
        Link createPurchaseLink = linkTo(methodOn(PurchasesController.class).createdPurchases(null, null)).withRel("Create-Purchase");
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");

        entityModel.add(prevCartListLink);
        entityModel.add(createCartLink);
        entityModel.add(removeSelectedCartLink);
        entityModel.add(createPurchaseLink);
        entityModel.add(allPurchaseListLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    /**
     * 카트 항목 상태 업데이트
     *
     * @param cartNo 업데이트할 카트 번호
     * @param session 현재 세션 정보
     * @return 204 NO CONTENT HTTP Status Code 반환
     */
    @PatchMapping("/carts/{cartNo}")
    public ResponseEntity updateCartItemStatus(@PathVariable int cartNo, HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 개별 장바구니 항목의 상태를 'Y'에서 'N'으로 업데이트 */
        cartsService.updateCartItemStatus(userNo,cartNo);

        return ResponseEntity.noContent().build();
    }

    /**
     * 모든 카트 항목 상태 업데이트
     *
     * @param session 현재 세션 정보
     * @return 204 NO CONTENT HTTP Status Code 반환
     */
    @PatchMapping("/carts")
    public ResponseEntity updateAllCartItemStatus(HttpSession session){
        /* 로그인 체크 */
        Long userNo = loginService.userLoginCheck(session);

        /* 전체 장바구니 항목의 상태를 'Y'에서 'N'으로 업데이트 */
        cartsService.updateAllCartItemStatus(userNo);

        return ResponseEntity.noContent().build();
    }

}
