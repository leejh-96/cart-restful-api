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

    public ProductsDTO createdProducts(ProductsDTO productsDTO) {
        productsDao.createdProducts(productsDTO);
        return productsDTO;
    }

    public int allProductsCount(PageInfo pageInfo) {

        return productsDao.allProductsCount(pageInfo);

    }

    public List<ProductsDTO> allProductsList(PageInfo pageInfo){
        return productsDao.allProductsPosts(pageInfo);
    }

    public ProductsDTO selectedProducts(int productNo) {

        ProductsDTO selectedProducts = productsDao.selectedProducts(productNo);

        //게시물이 존재하는지 확인
        if (selectedProducts == null)
            throw new ProductNotFoundException("존재하지 않는 상품 입니다.");

        return selectedProducts;
    }

}
