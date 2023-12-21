package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartItemsDTO;
import com.outliercart.restfulservice.dto.CartsDTO;
import com.outliercart.restfulservice.dto.ResponseDTO;
import com.outliercart.restfulservice.service.CartsService;
import com.outliercart.restfulservice.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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

    private final LoginService loginService;

    private final CartsService cartsService;

    public CartsController(LoginService loginService, CartsService cartsService) {
        this.loginService = loginService;
        this.cartsService = cartsService;
    }

    @PostMapping("/carts")
    public ResponseEntity<EntityModel<ResponseDTO>> createCarts(@Validated @RequestBody CartsDTO cartsDTO, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        cartsDTO.setUserNo(userNo);
        CartsDTO carts = cartsService.saveCart(cartsDTO);

        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("CartNo : "+carts.getCartNo()));

        Link allCartsListLink = linkTo(methodOn(this.getClass()).allCarts(null,null)).withRel("All-Carts-List");
        Link selectedCartsLink = linkTo(methodOn(this.getClass()).singleCarts(carts.getCartNo(), null,null)).withRel("Selected-Carts");

        entityModel.add(allCartsListLink);
        entityModel.add(selectedCartsLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @GetMapping("/carts")
    public ResponseEntity<EntityModel<ResponseDTO>> allCarts(@ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        pageInfo.setCount(cartsService.allCartsCount(userNo));
        pageInfo.pageSettings();

        List<CartItemsDTO> cartsList = cartsService.allCartsList(pageInfo, userNo);
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Carts List"));

        for (CartItemsDTO dto : cartsList){
            UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{cartNo}")
                    .replaceQueryParam("page", pageInfo.getPage())
                    .buildAndExpand(dto.getCartNo());

            Link productLink = Link.of(uriComponents.toUriString(), "Selected-Carts");
            entityModel.add(productLink);
        }

        for (CartItemsDTO dto : cartsList){
            UriComponents uriComponents = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/carts/{cartNo}")
                    .buildAndExpand(dto.getCartNo());

            Link removeSelectedCartLink = Link.of(uriComponents.toUriString(), "Remove-Selected-Carts");
            entityModel.add(removeSelectedCartLink);
        }

        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", pageInfo.getStartPage());

        Link startPageLink = Link.of(builder.toUriString(), "Start-Page");

        builder.replaceQueryParam("page", pageInfo.getPrevPage());
        Link prevPageLink = Link.of(builder.toUriString(), "Prev-Page");

        builder.replaceQueryParam("page", pageInfo.getPage());
        Link currentPageLink = Link.of(builder.toUriString(), "Current-Page");

        builder.replaceQueryParam("page", pageInfo.getNextPage());
        Link nextPageLink = Link.of(builder.toUriString(), "Next-Page");

        builder.replaceQueryParam("page", pageInfo.getEndPage());
        Link endPageLink = Link.of(builder.toUriString(), "End-Page");

        Link createCartsLink = linkTo(methodOn(this.getClass()).createCarts(null, null)).withRel("Create-Carts");
        Link removeAllCarts = linkTo(methodOn(this.getClass()).deleteAllCart(null)).withRel("Remove-All-Carts");
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");

        entityModel.add(startPageLink);
        entityModel.add(prevPageLink);
        entityModel.add(currentPageLink);
        entityModel.add(nextPageLink);
        entityModel.add(endPageLink);
        entityModel.add(createCartsLink);
        entityModel.add(removeAllCarts);
        entityModel.add(allPurchaseListLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @GetMapping("/carts/{cartNo}")
    public ResponseEntity<EntityModel<CartItemsDTO>> singleCarts(@PathVariable int cartNo, @ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);
        pageInfo.prevPageSettings();
        CartItemsDTO selectedCart = cartsService.singleCartsPosts(cartNo,userNo);

        EntityModel<CartItemsDTO> entityModel = EntityModel.of(selectedCart);

        UriComponentsBuilder cartsList = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/carts")
                .replaceQueryParam("page", pageInfo.getPage());

        Link prevCartListLink = Link.of(cartsList.toUriString(), "Prev-By-Cart-List");
        Link createCartLink = linkTo(methodOn(CartsController.class).createCarts(null, null)).withRel("Create-Carts");
        Link removeSelectedCartLink = linkTo(methodOn(CartsController.class).deleteSingleCart(selectedCart.getCartNo(), null)).withRel("Remove-Selected-Carts");
        Link createPurchaseLink = linkTo(methodOn(PurchasesController.class).purchaseItems(null, null)).withRel("Create-Purchase");
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");

        entityModel.add(prevCartListLink);
        entityModel.add(createCartLink);
        entityModel.add(removeSelectedCartLink);
        entityModel.add(createPurchaseLink);
        entityModel.add(allPurchaseListLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @PatchMapping("/carts/{cartNo}")
    public ResponseEntity deleteSingleCart(@PathVariable int cartNo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        cartsService.updateCartItemStatus(userNo,cartNo);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/carts")
    public ResponseEntity deleteAllCart(HttpSession session){

        Long userNo = loginService.userLoginCheck(session);
        cartsService.updateAllCartItemStatus(userNo);

        return ResponseEntity.noContent().build();
    }

}
