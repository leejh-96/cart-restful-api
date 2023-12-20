package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.ProductsRegisterDTO;
import com.outliercart.restfulservice.exception.PageNotFoundException;
import com.outliercart.restfulservice.exception.ProductNotFoundException;
import com.outliercart.restfulservice.repository.ProductsDao;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ProductService {

    private final ProductsDao productsDao;

    public ProductService(ProductsDao productsDao) {
        this.productsDao = productsDao;
    }

    public ProductsRegisterDTO save(ProductsRegisterDTO productsRegisterDTO) {
        productsDao.save(productsRegisterDTO);
        return productsRegisterDTO;
    }

    public int allProductsCount(PageInfo pageInfo) {
        return productsDao.allProductsCount(pageInfo);
    }

    public Map<String,Object> allProductsPosts(PageInfo pageInfo) {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("currentPage",pageInfo.getPage());
        map.put("prevPage",pageInfo.getPrevPage());
        map.put("nextPage",pageInfo.getNextPage());
        map.put("startPage",pageInfo.getStartPage());
        map.put("endPage",pageInfo.getEndPage());
        map.put("allProductPosts",productsDao.allProductsPosts(pageInfo));
        return map;
    }

    private void pageExistsCheck(PageInfo pageInfo) {
        if (pageInfo.getPage() > pageInfo.getEndPage())
            throw new PageNotFoundException("존재하지 않는 페이지 입니다.");
    }

    public ProductsRegisterDTO singleProductsPosts(int productNo) {
        ProductsRegisterDTO singleProductsPosts = productsDao.singleProductsPosts(productNo);

        //게시물이 존재하는지 확인
        postExistsCheck(singleProductsPosts);

        return singleProductsPosts;
    }

    private void postExistsCheck(ProductsRegisterDTO singleProductsPosts){
        if (singleProductsPosts == null)
            throw new ProductNotFoundException("존재하지 않는 상품 입니다.");
    }
}
