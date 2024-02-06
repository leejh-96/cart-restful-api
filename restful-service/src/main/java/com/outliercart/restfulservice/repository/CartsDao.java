package com.outliercart.restfulservice.repository;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartItemsDTO;
import com.outliercart.restfulservice.dto.CartsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CartsDao {

    void createdCarts(CartsDTO cartsDTO);

    void updateCartItemStatus(@Param("params") Map<String, Object> params);

    void updateAllCartItemStatus(Long userNo);

    int allCartsCount(Long userNo);

    List<CartItemsDTO> allCartsItems(@Param("pageInfo") PageInfo pageInfo,@Param("userNo") Long userNo);

    CartsDTO findByCartItem(@Param("params") Map<String, Object> params);

    CartItemsDTO selectedCarts(@Param("params") Map<String, Object> params);
}
