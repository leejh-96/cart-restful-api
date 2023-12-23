package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartItemsDTO;
import com.outliercart.restfulservice.dto.CartsDTO;
import com.outliercart.restfulservice.exception.ProductNotFoundException;
import com.outliercart.restfulservice.repository.CartsDao;
import com.outliercart.restfulservice.repository.ProductsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CartsService {

    private final CartsDao cartsDao;

    private final ProductsDao productsDao;

    public CartsService(CartsDao cartsDao, ProductsDao productsDao) {
        this.cartsDao = cartsDao;
        this.productsDao = productsDao;
    }

    public CartsDTO createdCarts(CartsDTO cartsDTO) {

        CartsDTO cartDTO = productsDao.findByProduct(cartsDTO.getProductNo());
        //상품이 존재하는지 체크
        if (cartDTO.getCount() == 0)
            throw new ProductNotFoundException("존재하지 않는 상품 입니다.");

        //상품의 수량이 있는지 체크
        if (cartDTO.getProductQuantity() < cartsDTO.getProductQuantity() || cartDTO.getProductQuantity() == 0)
            throw new ProductNotFoundException("현재 수량: "+cartDTO.getProductQuantity()+"개, 수량 부족으로 장바구니에 담을 수 없습니다.");

        //상품 장바구니에 담기
        cartsDao.createdCarts(cartsDTO);

        return cartsDTO;
    }

    public int allCartsCount(Long userNo) {
        return cartsDao.allCartsCount(userNo);
    }

    public List<CartItemsDTO> allCartsList(PageInfo pageInfo, Long userNo){
        return cartsDao.allCartsItems(pageInfo, userNo);
    }

    public void updateCartItemStatus(Long userNo, int cartNo) {

        Map<String, Object> params = createParams(userNo,cartNo);
        CartsDTO cartsDTO = cartsDao.findByCartItem(params);

        //장바구니에 선택한 상품이 있는지 체크
        if (cartsDTO == null)
            throw new ProductNotFoundException("장바구니에 해당 상품이 존재하지 않습니다.");

        //선택한 장바구니 상품 삭제
        cartsDao.updateCartItemStatus(params);
    }

    public void updateAllCartItemStatus(Long userNo) {

        cartsDao.updateAllCartItemStatus(userNo);

    }

    public CartItemsDTO selectedCarts(int cartNo, Long userNo) {

        Map<String, Object> params = createParams(userNo, cartNo);
        CartItemsDTO cartItemsDTO = cartsDao.selectedCarts(params);

        if (cartItemsDTO == null)
            throw new ProductNotFoundException(cartNo + "번 장바구니가 존재하지 않습니다.");

        return cartItemsDTO;
    }

    private Map<String, Object> createParams(Long userNo, int cartNo) {

        Map<String, Object> params = new HashMap<>();
        params.put("userNo",userNo);
        params.put("cartNo",cartNo);

        return params;
    }

}
