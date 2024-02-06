package com.outliercart.restfulservice.dbtest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseConnectionTest {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Test
    public void DbTest() throws SQLException {
        log.info("url: " + url);
        log.info("username: " + username);
        log.info("password: " + password);
        log.info("aws rds instance : {}", DriverManager.getConnection(url,username,password).toString());
    }
}
