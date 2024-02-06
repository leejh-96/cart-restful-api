package com.outliercart.restfulservice.repository;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.PurchaseItemDTO;
import com.outliercart.restfulservice.dto.PurchasesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PurchasesDao {

    PurchaseItemDTO findByCartItems(PurchaseItemDTO purchaseItemDTO);

    void createdPurchases(PurchaseItemDTO itemsDTO);

    void updateProductQuantity(PurchaseItemDTO itemsDTO);

    void updateCartsStatus(PurchaseItemDTO itemsDTO);

    int allPurchasesCount(Long userNo);

    List<PurchasesDTO> allPurchasesList(@Param("pageInfo") PageInfo pageInfo, @Param("userNo") Long userNo);

    PurchasesDTO selectedPurchases(@Param("params") Map<String, Object> params);
}
