package com.outliercart.restfulservice.service;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.stereotype.Service;

@Service
public class EncryptPasswordService {

    public String encryptPassword(String password) {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);
        return encryptedPassword;
    }

    public boolean checkPassword(String password, String encryptedPassword) {
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        return passwordEncryptor.checkPassword(password, encryptedPassword);
    }
}
