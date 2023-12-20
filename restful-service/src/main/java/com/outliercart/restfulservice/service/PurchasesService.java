package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.PurchasesItemsDTO;
import com.outliercart.restfulservice.dto.PurchasesListDTO;
import com.outliercart.restfulservice.exception.ProductNotFoundException;
import com.outliercart.restfulservice.exception.ProductQuantityException;
import com.outliercart.restfulservice.repository.PurchasesDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchasesService {

    private final PurchasesDao purchasesDao;

    @Transactional
    public PurchasesItemsDTO purchaseItems(PurchasesItemsDTO purchasesItemsDTO) {

        PurchasesItemsDTO itemsDTO = purchasesDao.findByCartItems(purchasesItemsDTO);

        if (itemsDTO == null)
            throw new ProductNotFoundException("장바구니에 해당 상품이 존재하지 않습니다.");

        if (itemsDTO.getProductQuantity() < itemsDTO.getCartQuantity()||
            itemsDTO.getProductQuantity() <= 0)
            throw new ProductQuantityException("현재 수량: "+itemsDTO.getProductQuantity()+"개, 수량 부족으로 주문할 수 없습니다.");

        //insert
        itemsDTO.setPurchaseDate(LocalDateTime.now());
        purchasesDao.savePurchaseItems(itemsDTO);

        //update
        purchasesDao.updateProductQuantity(itemsDTO);

        //update
        purchasesDao.updateCartsStatus(itemsDTO);

        return itemsDTO;
    }

    public int allPurchasesCount(Long userNo) {
        return purchasesDao.allPurchasesCount(userNo);
    }

    public Map<String,Object> allPurchasesPosts(PageInfo pageInfo, Long userNo) {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("currentPage",pageInfo.getPage());
        map.put("prevPage",pageInfo.getPrevPage());
        map.put("nextPage",pageInfo.getNextPage());
        map.put("startPage",pageInfo.getStartPage());
        map.put("endPage",pageInfo.getEndPage());
        map.put("allPurchasesPosts",purchasesDao.allPurchasesPosts(pageInfo,userNo));
        return map;
    }

    public PurchasesListDTO singlePurchasesPosts(int purchasesItemsNo, Long userNo) {
        Map<String, Object> params = createParams(userNo, purchasesItemsNo);
        PurchasesListDTO purchasesListDTO = purchasesDao.singlePurchasesPosts(params);
        if (purchasesListDTO == null)
            throw new ProductNotFoundException("구매 상품이 존재하지 않습니다.");
        return purchasesListDTO;
    }

    private Map<String, Object> createParams(Long userNo, int purchasesItemsNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userNo",userNo);
        params.put("purchasesItemsNo",purchasesItemsNo);
        return params;
    }

}
