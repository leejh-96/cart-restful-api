package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.PurchaseItemDTO;
import com.outliercart.restfulservice.dto.PurchasesDTO;
import com.outliercart.restfulservice.exception.ProductNotFoundException;
import com.outliercart.restfulservice.exception.ProductQuantityException;
import com.outliercart.restfulservice.repository.PurchasesDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchasesService {

    private final PurchasesDao purchasesDao;

    @Transactional
    public PurchaseItemDTO createdPurchases(PurchaseItemDTO purchaseItemDTO) {

        purchaseItemDTO = purchasesDao.findByCartItems(purchaseItemDTO);

        if (purchaseItemDTO == null)
            throw new ProductNotFoundException("장바구니에 해당 상품이 존재하지 않습니다.");

        if (purchaseItemDTO.getProductQuantity() < purchaseItemDTO.getCartQuantity() || purchaseItemDTO.getProductQuantity() <= 0)
            throw new ProductQuantityException("현재 수량: "+ purchaseItemDTO.getProductQuantity()+"개, 수량 부족으로 주문할 수 없습니다.");

        //insert
        purchaseItemDTO.setPurchaseDate(LocalDateTime.now());
        purchasesDao.createdPurchases(purchaseItemDTO);

        //update
        purchasesDao.updateProductQuantity(purchaseItemDTO);

        //update
        purchasesDao.updateCartsStatus(purchaseItemDTO);

        return purchaseItemDTO;
    }

    public int allPurchasesCount(Long userNo) {
        return purchasesDao.allPurchasesCount(userNo);
    }

    public List<PurchasesDTO> allPurchasesList(PageInfo pageInfo, Long userNo){
        return purchasesDao.allPurchasesList(pageInfo, userNo);
    }

    public PurchasesDTO selectedPurchases(int purchasesItemsNo, Long userNo) {

        Map<String, Object> params = createParams(userNo, purchasesItemsNo);
        PurchasesDTO purchasesDTO = purchasesDao.selectedPurchases(params);

        if (purchasesDTO == null)
            throw new ProductNotFoundException("구매 상품이 존재하지 않습니다.");

        return purchasesDTO;
    }

    private Map<String, Object> createParams(Long userNo, int purchasesItemsNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userNo",userNo);
        params.put("purchasesItemsNo",purchasesItemsNo);
        return params;
    }

}
