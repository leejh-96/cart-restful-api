package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.dto.UsersRegisterDTO;
import com.outliercart.restfulservice.exception.UsersRegisterException;
import com.outliercart.restfulservice.repository.UsersRegisterDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UsersRegisterService {
    
    private final EncryptPasswordService passwordService;

    private final UsersRegisterDao usersRegisterDao;

    public UsersRegisterService(EncryptPasswordService passwordService, UsersRegisterDao usersRegisterDao) {
        this.passwordService = passwordService;
        this.usersRegisterDao = usersRegisterDao;
    }

    public UsersRegisterDTO save(UsersRegisterDTO usersRegisterDTO) {

        //사용중인 ID 인지, 사용중인 EMAIL 인지 체크.
        duplicateIdCheck(usersRegisterDTO.getUserId());
        duplicateEmailCheck(usersRegisterDTO.getUserEmail());

        //jasypt 라이브러리를 사용하여 비밀번호 암호화.
        String encryptPassword = passwordService.encryptPassword(usersRegisterDTO.getUserPassword());
        usersRegisterDTO.setUserPassword(encryptPassword);

        //DB 저장.
        usersRegisterDao.save(usersRegisterDTO);
        return usersRegisterDTO;
    }

    private void duplicateIdCheck(String userId){
        if (usersRegisterDao.findById(userId) != 0)
            throw new UsersRegisterException("이미 사용중인 아이디입니다.");
    }

    private void duplicateEmailCheck(String userEmail){
        if (usersRegisterDao.findByEmail(userEmail) != 0)
            throw new UsersRegisterException("이미 사용중인 이메일입니다.");
    }

}
