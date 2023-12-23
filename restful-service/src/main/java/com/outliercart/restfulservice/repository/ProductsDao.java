package com.outliercart.restfulservice.repository;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.CartsDTO;
import com.outliercart.restfulservice.dto.ProductsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductsDao {

    void createdProducts(ProductsDTO productsDTO);

    int allProductsCount(PageInfo pageInfo);

    List<ProductsDTO> allProductsPosts(PageInfo pageInfo);

    ProductsDTO selectedProducts(int productNo);

    CartsDTO findByProduct(Integer productNo);
}
