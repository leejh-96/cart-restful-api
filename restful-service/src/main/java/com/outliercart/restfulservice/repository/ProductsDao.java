package com.outliercart.restfulservice.repository;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartsDTO;
import com.outliercart.restfulservice.dto.ProductsRegisterDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductsDao {

    void save(ProductsRegisterDTO productsRegisterDTO);

    int allProductsCount(PageInfo pageInfo);

    List<ProductsRegisterDTO> allProductsPosts(PageInfo pageInfo);

    ProductsRegisterDTO singleProductsPosts(int productNo);

    CartsDTO findByProduct(Integer productNo);
}
