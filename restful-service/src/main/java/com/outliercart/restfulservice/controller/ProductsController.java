package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.ProductsRegisterDTO;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

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
    public ResponseEntity<ProductsRegisterDTO> createProducts(@Validated @RequestBody ProductsRegisterDTO productsRegisterDTO,
                                                              HttpSession session){

        loginService.userLoginCheck(session);

        ProductsRegisterDTO productDTO = productService.save(productsRegisterDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                            .path("/{search}")
                                            .buildAndExpand(productDTO.getProductNo())
                                            .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String,Object>> allProducts(@ModelAttribute PageInfo pageInfo){

        pageInfo.setCount(productService.allProductsCount(pageInfo));
        pageInfo.pageSettings();

        return ResponseEntity.status(HttpStatus.OK).body(productService.allProductsPosts(pageInfo));
    }

    @GetMapping("/products/{productNo}")
    public ResponseEntity<ProductsRegisterDTO> singleProducts(@PathVariable int productNo){

        ProductsRegisterDTO singleProductDTO = productService.singleProductsPosts(productNo);

        return ResponseEntity.status(HttpStatus.OK).body(singleProductDTO);
    }

}
