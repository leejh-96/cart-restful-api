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

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param usersRegisterDTO 등록할 사용자 정보
     * @return 등록된 사용자 정보
     * @throws UsersRegisterException 이미 사용 중인 아이디 또는 이메일일 때 예외 처리
     */
    public UsersRegisterDTO createdUsers(UsersRegisterDTO usersRegisterDTO) {
        /* 사용 중인 아이디 체크 후 예외 처리 */
        if (usersRegisterDao.findById(usersRegisterDTO.getUserId()) != 0)
            throw new UsersRegisterException("이미 사용중인 아이디입니다.");

        /* 사용 중인 이메일 체크 후 예외 처리 */
        if (usersRegisterDao.findByEmail(usersRegisterDTO.getUserEmail()) != 0)
            throw new UsersRegisterException("이미 사용중인 이메일입니다.");

        /* 비밀번호 암호화 */
        String encryptPassword = passwordService.encryptPassword(usersRegisterDTO.getUserPassword());
        usersRegisterDTO.setUserPassword(encryptPassword);

        /* 사용자 정보 저장 */
        usersRegisterDao.createdUsers(usersRegisterDTO);

        return usersRegisterDTO;
    }

}
