package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.dto.UsersRegisterDTO;
import com.outliercart.restfulservice.exception.UsersRegisterException;
import com.outliercart.restfulservice.repository.UsersRegisterDao;
import org.springframework.stereotype.Service;

@Service
public class UsersRegisterService {
    
    private final EncryptPasswordService passwordService;

    private final UsersRegisterDao usersRegisterDao;

    public UsersRegisterService(EncryptPasswordService passwordService, UsersRegisterDao usersRegisterDao) {
        this.passwordService = passwordService;
        this.usersRegisterDao = usersRegisterDao;
    }

    public UsersRegisterDTO createdUsers(UsersRegisterDTO usersRegisterDTO) {

        //사용중인 ID 인지, 사용중인 EMAIL 인지 체크.
        if (usersRegisterDao.findById(usersRegisterDTO.getUserId()) != 0)
            throw new UsersRegisterException("이미 사용중인 아이디입니다.");

        if (usersRegisterDao.findByEmail(usersRegisterDTO.getUserEmail()) != 0)
            throw new UsersRegisterException("이미 사용중인 이메일입니다.");

        //jasypt 라이브러리를 사용하여 비밀번호 암호화.
        String encryptPassword = passwordService.encryptPassword(usersRegisterDTO.getUserPassword());
        usersRegisterDTO.setUserPassword(encryptPassword);

        //DB 저장.
        usersRegisterDao.createdUsers(usersRegisterDTO);
        return usersRegisterDTO;
    }

}
