package com.outliercart.restfulservice.service;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.stereotype.Service;

@Service
public class EncryptPasswordService {

    /**
     * 입력받은 패스워드를 암호화합니다.
     *
     * @param password 암호화할 패스워드
     * @return 암호화된 패스워드
     */
    public String encryptPassword(String password) {
        /* BasicPasswordEncryptor 인스턴스 생성하여 입력받은 패스워드 암호화 후 반환 */
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);
        return encryptedPassword;
    }

    /**
     * 입력받은 패스워드와 암호화된 패스워드를 비교합니다.
     *
     * @param password 사용자가 입력한 패스워드
     * @param encryptedPassword 저장된 암호화된 패스워드
     * @return 패스워드 일치 여부
     */
    public boolean checkPassword(String password, String encryptedPassword) {
        /* BasicPasswordEncryptor 인스턴스를 통해 입력받은 패스워드와 암호화된 패스워드 비교 후 결과 반환 */
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        return passwordEncryptor.checkPassword(password, encryptedPassword);
    }
}
