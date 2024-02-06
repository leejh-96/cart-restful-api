package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.ProductsDTO;
import com.outliercart.restfulservice.exception.ProductNotFoundException;
import com.outliercart.restfulservice.repository.ProductsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductService {

    private final ProductsDao productsDao;

    public ProductService(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

    /**
     * 새로운 상품을 생성합니다.
     *
     * @param productsDTO 생성할 상품 정보
     * @return 생성된 상품 정보
     */
    public ProductsDTO createdProducts(ProductsDTO productsDTO) {
        /* 상품 생성 후 정보 반환 */
        productsDao.createdProducts(productsDTO);
        return productsDTO;
    }

    /**
     * 모든 상품의 수를 반환합니다.
     *
     * @param pageInfo 페이지 정보
     * @return 전체 상품 수
     */
    public int allProductsCount(PageInfo pageInfo) {
        /* 모든 상품 수 반환 */
        return productsDao.allProductsCount(pageInfo);
    }

    /**
     * 모든 상품 목록을 반환합니다.
     *
     * @param pageInfo 페이지 정보
     * @return 모든 상품 목록
     */
    public List<ProductsDTO> allProductsList(PageInfo pageInfo){
        /* 모든 상품 목록 반환 */
        return productsDao.allProductsPosts(pageInfo);
    }

    /**
     * 특정 상품을 반환합니다.
     *
     * @param productNo 상품 번호
     * @return 특정 상품 정보
     * @throws ProductNotFoundException 상품이 존재하지 않을 때 예외 처리
     */
    public ProductsDTO selectedProducts(int productNo) {
        /* 특정 상품 반환 */
        ProductsDTO selectedProducts = productsDao.selectedProducts(productNo);

        /* 게시물이 존재하는지 확인 */
        if (selectedProducts == null)
            throw new ProductNotFoundException("존재하지 않는 상품 입니다.");

        return selectedProducts;
    }

}
