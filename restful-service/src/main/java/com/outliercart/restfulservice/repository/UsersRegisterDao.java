package com.outliercart.restfulservice.repository;

import com.outliercart.restfulservice.dto.UsersRegisterDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UsersRegisterDao {

    int findById(String userId);

    int findByEmail(String userEmail);

    void createdUsers(UsersRegisterDTO usersRegisterDTO);
}
