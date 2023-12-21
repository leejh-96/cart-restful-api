package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.ProductsRegisterDTO;
import com.outliercart.restfulservice.dto.ResponseDTO;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.ProductService;
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

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
public class ProductsController {

    private final LoginService loginService;

    private final ProductService productService;

    public ProductsController(LoginService loginService, ProductService productService) {
        this.loginService = loginService;
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<EntityModel<ResponseDTO>> createProducts(@Validated @RequestBody ProductsRegisterDTO productsRegisterDTO,
                                                              HttpSession session){

        loginService.userLoginCheck(session);
        ProductsRegisterDTO productDTO = productService.save(productsRegisterDTO);

        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("ProductNo : "+productDTO.getProductNo()));

        Link allProductsListLink = linkTo(methodOn(this.getClass()).allProducts(null)).withRel("All-Products-List");
        Link selectedProductsLink = linkTo(methodOn(this.getClass()).singleProducts(productDTO.getProductNo(), null)).withRel("Selected-Products");

        entityModel.add(allProductsListLink);
        entityModel.add(selectedProductsLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @GetMapping("/products")
    public ResponseEntity<EntityModel<ResponseDTO>> allProducts(@ModelAttribute PageInfo pageInfo){

        pageInfo.setCount(productService.allProductsCount(pageInfo));
        pageInfo.pageSettings();

        List<ProductsRegisterDTO> productsList = productService.allProductsList(pageInfo);
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Products Search Result"));

        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("searchType", pageInfo.getSearchType())
                .replaceQueryParam("searchContent", pageInfo.getSearchContent())
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

        for (ProductsRegisterDTO dto : productsList){
            UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{productNo}")
                    .replaceQueryParam("searchType", pageInfo.getSearchType())
                    .replaceQueryParam("searchContent", pageInfo.getSearchContent())
                    .replaceQueryParam("page", pageInfo.getPage())
                    .buildAndExpand(dto.getProductNo());

            Link productLink = Link.of(uriComponents.toUriString(), "Selected-Products");
            entityModel.add(productLink);
        }

        Link createProductsLink = linkTo(methodOn(this.getClass()).createProducts(null, null)).withRel("Create-Products");
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null, null)).withRel("All-Carts-List");
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");

        entityModel.add(startPageLink);
        entityModel.add(prevPageLink);
        entityModel.add(currentPageLink);
        entityModel.add(nextPageLink);
        entityModel.add(endPageLink);
        entityModel.add(createProductsLink);
        entityModel.add(allCartsListLink);
        entityModel.add(allPurchaseListLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @GetMapping("/products/{productNo}")
    public ResponseEntity<EntityModel<ProductsRegisterDTO>> singleProducts(@PathVariable int productNo, @ModelAttribute PageInfo pageInfo){

        pageInfo.prevPageSettings();
        ProductsRegisterDTO selectedProduct = productService.singleProductsPosts(productNo);

        EntityModel<ProductsRegisterDTO> entityModel = EntityModel.of(selectedProduct);

        UriComponentsBuilder productsList = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/products")
                .replaceQueryParam("searchType", pageInfo.getSearchType())
                .replaceQueryParam("searchContent", pageInfo.getSearchContent())
                .replaceQueryParam("page", pageInfo.getPage());

        Link prevProductListLink = Link.of(productsList.toUriString(), "Prev-By-Product-List");
        Link createCartLink = linkTo(methodOn(CartsController.class).createCarts(null, null)).withRel("Create-Carts");

        entityModel.add(prevProductListLink);
        entityModel.add(createCartLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
