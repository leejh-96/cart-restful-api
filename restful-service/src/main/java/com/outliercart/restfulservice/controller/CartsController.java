package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartItemsDTO;
import com.outliercart.restfulservice.dto.CartsDTO;
import com.outliercart.restfulservice.service.CartsService;
import com.outliercart.restfulservice.service.LoginService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

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
    public ResponseEntity<CartsDTO> createCarts(@Validated @RequestBody CartsDTO cartsDTO, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        cartsDTO.setUserNo(userNo);
        CartsDTO carts = cartsService.saveCart(cartsDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                            .path("/{cartNo}")
                                            .buildAndExpand(carts.getCartNo())
                                            .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/carts")
    public ResponseEntity<Map<String,Object>> allCarts(@ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        pageInfo.setCount(cartsService.allCartsCount(userNo));
        pageInfo.pageSettings();

        return ResponseEntity.status(HttpStatus.OK).body(cartsService.allCartsItems(pageInfo,userNo));
    }

    @GetMapping("/carts/{cartNo}")
    public ResponseEntity<CartItemsDTO> singleCarts(@PathVariable int cartNo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        CartItemsDTO cartItemsDTO = cartsService.singleCartsPosts(cartNo,userNo);

        return ResponseEntity.status(HttpStatus.OK).body(cartItemsDTO);
    }

    @DeleteMapping("/carts/{cartNo}")
    public ResponseEntity deleteSingleCart(@PathVariable int cartNo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        cartsService.deleteSingleCart(userNo,cartNo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/carts")
    public ResponseEntity deleteAllCart(HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        cartsService.deleteAllCart(userNo);

        return ResponseEntity.noContent().build();
    }

}
