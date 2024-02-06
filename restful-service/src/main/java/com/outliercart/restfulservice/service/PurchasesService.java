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

    /**
     * 구매를 생성하고 처리하는 메서드입니다.
     *
     * @param purchaseItemDTO 구매할 상품 정보
     * @return 생성된 구매 정보
     * @throws ProductNotFoundException 장바구니에 해당 상품이 존재하지 않을 때 예외 처리
     * @throws ProductQuantityException 수량 부족으로 주문할 수 없을 때 예외 처리
     */
    @Transactional
    public PurchaseItemDTO createdPurchases(PurchaseItemDTO purchaseItemDTO) {
        /* 장바구니에 구매하고자 하는 아이템을 데이터베이스로부터 반환 */
        purchaseItemDTO = purchasesDao.findByCartItems(purchaseItemDTO);

        /* 장바구니에 해당 아이템이 존재하는지 예외 처리 */
        if (purchaseItemDTO == null)
            throw new ProductNotFoundException("장바구니에 해당 상품이 존재하지 않습니다.");

        /* 수량 확인 후 예외 처리 */
        if (purchaseItemDTO.getProductQuantity() < purchaseItemDTO.getCartQuantity() || purchaseItemDTO.getProductQuantity() <= 0)
            throw new ProductQuantityException("현재 수량: "+ purchaseItemDTO.getProductQuantity()+"개, 수량 부족으로 주문할 수 없습니다.");

        /* 구매 주문 생성 */
        purchaseItemDTO.setPurchaseDate(LocalDateTime.now());
        purchasesDao.createdPurchases(purchaseItemDTO);

        /* 상품 수량 업데이트 */
        purchasesDao.updateProductQuantity(purchaseItemDTO);

        /* 장바구니 아이템 상태 'Y'에서 'N'으로 업데이트 */
        purchasesDao.updateCartsStatus(purchaseItemDTO);

        return purchaseItemDTO;
    }

    /**
     * 특정 사용자의 전체 구매 수를 반환합니다.
     *
     * @param userNo 사용자 번호
     * @return 전체 구매 수
     */
    public int allPurchasesCount(Long userNo) {
        return purchasesDao.allPurchasesCount(userNo);
    }

    /**
     * 특정 사용자의 전체 구매 목록을 반환합니다.
     *
     * @param pageInfo 페이지 정보
     * @param userNo 사용자 번호
     * @return 전체 구매 목록
     */
    public List<PurchasesDTO> allPurchasesList(PageInfo pageInfo, Long userNo){
        return purchasesDao.allPurchasesList(pageInfo, userNo);
    }

    /**
     * 특정 구매 상품을 반환합니다.
     *
     * @param purchasesItemsNo 구매 상품 번호
     * @param userNo 사용자 번호
     * @return 특정 구매 상품 정보
     * @throws ProductNotFoundException 상품이 존재하지 않을 때 예외 처리
     */
    public PurchasesDTO selectedPurchases(int purchasesItemsNo, Long userNo) {
        /* userNo와 cartNo를 이용하여 Map<String, Object> 형태의 파라미터를 생성 */
        Map<String, Object> params = createParams(userNo, purchasesItemsNo);

        /* 데이터베이스에서 구매 아이템에 대한 정보를 반환 */
        PurchasesDTO purchasesDTO = purchasesDao.selectedPurchases(params);

        /* 구매 아이템이 있는지 체크 */
        if (purchasesDTO == null)
            throw new ProductNotFoundException("구매 상품이 존재하지 않습니다.");

        return purchasesDTO;
    }

    /**
     * 매개변수를 이용해 맵을 생성합니다.
     *
     * @param userNo 사용자 번호
     * @param purchasesItemsNo 구매 아이템 번호
     * @return 생성된 맵
     */
    private Map<String, Object> createParams(Long userNo, int purchasesItemsNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userNo",userNo);
        params.put("purchasesItemsNo",purchasesItemsNo);
        return params;
    }

}
