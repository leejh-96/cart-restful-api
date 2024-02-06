package com.outliercart.restfulservice.repository;

import com.outliercart.restfulservice.dto.LoginDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LoginDao {

    LoginDTO findByUser(String userId);
}
