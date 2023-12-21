package com.outliercart.restfulservice.repository;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartItemsDTO;
import com.outliercart.restfulservice.dto.ProductsRegisterDTO;
import com.outliercart.restfulservice.dto.PurchasesItemsDTO;
import com.outliercart.restfulservice.dto.PurchasesListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PurchasesDao {

//    void purchaseProduct(List<PurchasesDTO> purchasesDTO);//결제테이블 insert


//    List<PurchasesItemsDTO> findByCartList(@Param("userNo") Long userNo, @Param("cartNo") List<Integer> cartNo);

//    List<PurchasesItemsDTO> findByCartList(@Param("userNo") Long userNo, @Param("purchasesDTO") List<PurchasesItemsDTO> purchasesDTO);
//    void updateProductQuantity(List<PurchasesItemsDTO> purchasesItemList);
//
//    List<CartItemsDTO> findByProductList(List<CartItemsDTO> list);

    PurchasesItemsDTO findByCartItems(PurchasesItemsDTO purchasesItemsDTO);

    void savePurchaseItems(PurchasesItemsDTO itemsDTO);

    void updateProductQuantity(PurchasesItemsDTO itemsDTO);

    void updateCartsStatus(PurchasesItemsDTO itemsDTO);

    int allPurchasesCount(Long userNo);

    List<PurchasesListDTO> allPurchasesList(@Param("pageInfo") PageInfo pageInfo, @Param("userNo") Long userNo);

    PurchasesListDTO singlePurchasesPosts(@Param("params") Map<String, Object> params);
}
