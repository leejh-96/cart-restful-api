package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.PurchasesItemsDTO;
import com.outliercart.restfulservice.dto.PurchasesListDTO;
import com.outliercart.restfulservice.exception.UserNotFoundException;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.PurchasesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PurchasesController {

    private final LoginService loginService;

    private final PurchasesService purchasesService;

    @PostMapping("/purchases-items")
    public ResponseEntity<PurchasesItemsDTO> purchaseItems(@RequestBody PurchasesItemsDTO purchasesItemsDTO, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        if (!userNo.equals(purchasesItemsDTO.getUserNo()))
            throw new UserNotFoundException("일치하지 않은 사용자입니다.");

        PurchasesItemsDTO itemsDTO = purchasesService.purchaseItems(purchasesItemsDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                            .path("/{purchasesItemsNo}")
                                            .buildAndExpand(itemsDTO.getPurchasesItemsNo())
                                            .toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/purchases-items")
    public ResponseEntity<Map<String,Object>> allPurchasesItems(@ModelAttribute PageInfo pageInfo, HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        pageInfo.setCount(purchasesService.allPurchasesCount(userNo));
        pageInfo.pageSettings();

        return ResponseEntity.status(HttpStatus.OK).body(purchasesService.allPurchasesPosts(pageInfo,userNo));
    }

    @GetMapping("/purchases-items/{purchasesItemsNo}")
    public ResponseEntity<PurchasesListDTO> singlePurchasesItems(@PathVariable int purchasesItemsNo,HttpSession session){

        Long userNo = loginService.userLoginCheck(session);

        PurchasesListDTO singlePurchasesDTO = purchasesService.singlePurchasesPosts(purchasesItemsNo,userNo);

        return ResponseEntity.status(HttpStatus.OK).body(singlePurchasesDTO);
    }

}
