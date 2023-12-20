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
import java.util.LinkedHashMap;
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

    public CartsDTO saveCart(CartsDTO cartsDTO) {

        //상품이 존재하는지 체크
        CartsDTO cartDTO = productsDao.findByProduct(cartsDTO.getProductNo());
        productExistsCheck(cartDTO);

        //상품의 수량이 있는지 체크
        productQuantityCheck(cartDTO,cartsDTO);

        //상품 장바구니에 담기
        cartsDao.saveCart(cartsDTO);

        return cartsDTO;
    }

    public int allCartsCount(Long userNo) {
        int cartsCount = cartsDao.allCartsCount(userNo);
        if (cartsCount == 0)
            throw new ProductNotFoundException("장바구니가 비어있습니다.");
        return cartsCount;
    }

    public Map<String,Object> allCartsItems(PageInfo pageInfo, Long userNo) {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("currentPage",pageInfo.getPage());
        map.put("prevPage",pageInfo.getPrevPage());
        map.put("nextPage",pageInfo.getNextPage());
        map.put("startPage",pageInfo.getStartPage());
        map.put("endPage",pageInfo.getEndPage());
        map.put("allProductPosts",cartsDao.allCartsItems(pageInfo,userNo));
        return map;
    }

    private void productQuantityCheck(CartsDTO cartDTO, CartsDTO cartsDTO) {
        if (cartDTO.getProductQuantity() < cartsDTO.getProductQuantity()
                ||cartDTO.getProductQuantity() == 0)
            throw new ProductNotFoundException("현재 수량: "+cartDTO.getProductQuantity()+"개, 수량 부족으로 장바구니에 담을 수 없습니다.");
    }

    private void productExistsCheck(CartsDTO cartDTO){
        if (cartDTO.getCount() == 0)
            throw new ProductNotFoundException("존재하지 않는 상품 입니다.");
    }

    public void deleteSingleCart(Long userNo, int cartNo) {
        Map<String, Object> params = createParams(userNo,cartNo);
        CartsDTO cartsDTO = cartsDao.findByCartItem(params);

        //장바구니에 선택한 상품이 있는지 체크
        cartExistsCheck(cartsDTO);

        //선택한 장바구니 상품 삭제
        cartsDao.deleteSingleCart(params);
    }

    public CartItemsDTO singleCartsPosts(int cartNo, Long userNo) {
        Map<String, Object> params = createParams(userNo, cartNo);
        CartItemsDTO cartItemsDTO = cartsDao.singleCartsPosts(params);
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

    public void deleteAllCart(Long userNo) {
        cartsDao.deleteAllCart(userNo);
    }

    private void cartExistsCheck(CartsDTO cartDTO){
        if (cartDTO == null)
            throw new ProductNotFoundException("장바구니에 해당 상품이 존재하지 않습니다.");
    }

}
