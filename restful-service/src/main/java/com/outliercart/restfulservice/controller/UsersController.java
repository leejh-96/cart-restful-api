package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.dto.LoginDTO;
import com.outliercart.restfulservice.dto.UsersRegisterDTO;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.UsersRegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class UsersController {

    private final LoginService loginService;

    private final UsersRegisterService usersRegisterService;

    public UsersController(LoginService loginService, UsersRegisterService usersRegisterService) {
        this.loginService = loginService;
        this.usersRegisterService = usersRegisterService;
    }

    @PostMapping("/users")
    public ResponseEntity<UsersRegisterDTO> createUser(@Validated @RequestBody UsersRegisterDTO usersRegisterDTO){

        usersRegisterService.save(usersRegisterDTO);

//        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
//                                            .path("/login")
//                                            .buildAndExpand()
//                                            .toUri();

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                                            .path("/login")
                                            .build()
                                            .toUri();
//        URI uri = ServletUriComponentsBuilder.fromPath("/login").build().toUri();

        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/users/login")
    public ResponseEntity<LoginDTO> login(@Validated @RequestBody LoginDTO loginDTO, HttpSession session){

        loginService.login(loginDTO,session);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/users/logout")
    public ResponseEntity logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session != null){
            session.invalidate();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }




}
