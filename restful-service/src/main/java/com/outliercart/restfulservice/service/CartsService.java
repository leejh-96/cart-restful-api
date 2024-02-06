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

    /**
     * 새로운 장바구니를 생성합니다.
     *
     * @param cartsDTO 생성할 장바구니 정보
     * @return 생성된 장바구니 정보
     * @throws ProductNotFoundException 장바구니에 담기를 시도한 상품이 존재하지 않거나 수량이 부족할 때 발생하는 예외
     */
    public CartsDTO createdCarts(CartsDTO cartsDTO) {
        /* 데이터베이스에서 상품에 대한 정보 반환 */
        CartsDTO cartDTO = productsDao.findByProduct(cartsDTO.getProductNo());

        /* 상품이 존재하는지 체크 */
        if (cartDTO.getCount() == 0)
            throw new ProductNotFoundException("존재하지 않는 상품 입니다.");

        /* 상품의 수량이 있는지 체크 */
        if (cartDTO.getProductQuantity() < cartsDTO.getProductQuantity() || cartDTO.getProductQuantity() == 0)
            throw new ProductNotFoundException("현재 수량: "+cartDTO.getProductQuantity()+"개, 수량 부족으로 장바구니에 담을 수 없습니다.");

        /* 상품 장바구니에 담기 */
        cartsDao.createdCarts(cartsDTO);

        return cartsDTO;
    }

    /**
     * 특정 사용자의 장바구니 아이템 수를 반환합니다.
     *
     * @param userNo 사용자 번호
     * @return 장바구니 아이템 수
     */
    public int allCartsCount(Long userNo) {
        return cartsDao.allCartsCount(userNo);
    }

    /**
     * 특정 사용자의 모든 장바구니 아이템 목록을 반환합니다.
     *
     * @param pageInfo 페이지 정보
     * @param userNo 사용자 번호
     * @return 장바구니 아이템 목록
     */
    public List<CartItemsDTO> allCartsList(PageInfo pageInfo, Long userNo){
        return cartsDao.allCartsItems(pageInfo, userNo);
    }

    /**
     * 특정 사용자의 장바구니 아이템 상태를 'Y'에서 'N'으로 업데이트합니다.
     *
     * @param userNo 사용자 번호
     * @param cartNo 장바구니 번호
     * @throws ProductNotFoundException 업데이트를 시도한 장바구니 아이템이 존재하지 않을 때 발생하는 예외
     */
    public void updateCartItemStatus(Long userNo, int cartNo) {
        /* userNo와 cartNo를 이용하여 Map<String, Object> 형태의 파라미터를 생성 */
        Map<String, Object> params = createParams(userNo,cartNo);

        /* 데이터베이스에서 장바구니 아이템에 대한 정보를 반환 */
        CartsDTO cartsDTO = cartsDao.findByCartItem(params);

        /* 장바구니에 선택한 상품이 있는지 체크 */
        if (cartsDTO == null)
            throw new ProductNotFoundException("장바구니에 해당 상품이 존재하지 않습니다.");

        /* 선택한 장바구니 아이템 상태를 'Y'에서 'N'으로 업데이트 */
        cartsDao.updateCartItemStatus(params);
    }

    /**
     * 특정 사용자의 모든 장바구니 아이템 상태를 업데이트합니다.
     *
     * @param userNo 사용자 번호
     */
    public void updateAllCartItemStatus(Long userNo) {
        /* 모든 장바구니 아이템 상태를 'Y'에서 'N'으로 업데이트 */
        cartsDao.updateAllCartItemStatus(userNo);
    }

    /**
     * 특정 장바구니 아이템을 반환합니다.
     *
     * @param cartNo 장바구니 번호
     * @param userNo 사용자 번호
     * @return 특정 장바구니 아이템 정보
     * @throws ProductNotFoundException 특정 장바구니 아이템이 존재하지 않을 때 발생하는 예외
     */
    public CartItemsDTO selectedCarts(int cartNo, Long userNo) {
        /* userNo와 cartNo를 이용하여 Map<String, Object> 형태의 파라미터를 생성 */
        Map<String, Object> params = createParams(userNo, cartNo);

        /* 데이터베이스에서 장바구니 아이템에 대한 정보를 반환 */
        CartItemsDTO cartItemsDTO = cartsDao.selectedCarts(params);

        /* 선택한 장바구니 아이템이 있는지 체크 */
        if (cartItemsDTO == null)
            throw new ProductNotFoundException(cartNo + "번 장바구니가 존재하지 않습니다.");

        return cartItemsDTO;
    }

    /**
     * 매개변수를 이용해 맵을 생성합니다.
     *
     * @param userNo 사용자 번호
     * @param cartNo 장바구니 번호
     * @return 생성된 맵
     */
    private Map<String, Object> createParams(Long userNo, int cartNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userNo",userNo);
        params.put("cartNo",cartNo);
        return params;
    }

}
