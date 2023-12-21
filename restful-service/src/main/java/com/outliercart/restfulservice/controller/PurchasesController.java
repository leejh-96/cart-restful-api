package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.*;
import com.outliercart.restfulservice.exception.UserNotFoundException;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.PurchasesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class PurchasesController {

    private final LoginService loginService;

    private final PurchasesService purchasesService;

    @PostMapping("/purchases-items")
    public ResponseEntity<EntityModel<ResponseDTO>> purchaseItems(@RequestBody PurchasesItemsDTO purchasesItemsDTO, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);
        if (!userNo.equals(purchasesItemsDTO.getUserNo()))
            throw new UserNotFoundException("일치하지 않은 사용자입니다.");
        purchasesItemsDTO = purchasesService.purchaseItems(purchasesItemsDTO);

        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("purchaseNo : "+purchasesItemsDTO.getPurchasesItemsNo()));

        Link allPurchasesListLink = linkTo(methodOn(this.getClass()).allPurchasesItems(null,null)).withRel("All-Purchases-List");
        Link selectedPurchasesLink = linkTo(methodOn(this.getClass()).singlePurchasesItems(purchasesItemsDTO.getPurchasesItemsNo(), null,null)).withRel("Selected-Purchases");
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");

        entityModel.add(allPurchasesListLink);
        entityModel.add(selectedPurchasesLink);
        entityModel.add(allProductsListLink);
        entityModel.add(allCartsListLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @GetMapping("/purchases-items")
    public ResponseEntity<EntityModel<ResponseDTO>> allPurchasesItems(@ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);
        pageInfo.setCount(purchasesService.allPurchasesCount(userNo));
        pageInfo.pageSettings();
        List<PurchasesListDTO> purchasesList = purchasesService.allPurchasesList(pageInfo, userNo);

        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Purchases List"));

        for (PurchasesListDTO dto : purchasesList){
            UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{purchasesItemsNo}")
                    .replaceQueryParam("page", pageInfo.getPage())
                    .buildAndExpand(dto.getPurchasesItemsNo());

            Link purchaseItemLink = Link.of(uriComponents.toUriString(), "Selected-Purchases-Item");
            entityModel.add(purchaseItemLink);
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

        Link createPurchaseLink = linkTo(methodOn(this.getClass()).purchaseItems(null,null)).withRel("Create-Purchase");
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null,null)).withRel("All-Carts-List");

        entityModel.add(startPageLink);
        entityModel.add(prevPageLink);
        entityModel.add(currentPageLink);
        entityModel.add(nextPageLink);
        entityModel.add(endPageLink);
        entityModel.add(allProductsListLink);
        entityModel.add(allCartsListLink);
        entityModel.add(createPurchaseLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @GetMapping("/purchases-items/{purchasesItemsNo}")
    public ResponseEntity<EntityModel<PurchasesListDTO>> singlePurchasesItems(@PathVariable int purchasesItemsNo, @ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);
        pageInfo.prevPageSettings();
        PurchasesListDTO selectedPurchaseItem = purchasesService.singlePurchasesPosts(purchasesItemsNo,userNo);

        EntityModel<PurchasesListDTO> entityModel = EntityModel.of(selectedPurchaseItem);

        UriComponentsBuilder productsList = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/purchases-items")
                .replaceQueryParam("page", pageInfo.getPage());

        Link createPurchaseLink = linkTo(methodOn(this.getClass()).purchaseItems(null,null)).withRel("Create-Purchase");
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
