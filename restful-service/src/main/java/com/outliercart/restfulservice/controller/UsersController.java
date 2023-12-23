package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.dto.ResponseDTO;
import com.outliercart.restfulservice.dto.LoginDTO;
import com.outliercart.restfulservice.dto.UsersRegisterDTO;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.UsersRegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UsersController {

    private final LoginService loginService;

    private final UsersRegisterService usersRegisterService;

    public UsersController(LoginService loginService, UsersRegisterService usersRegisterService) {
        this.loginService = loginService;
        this.usersRegisterService = usersRegisterService;
    }

    @PostMapping("/users")
    public ResponseEntity<EntityModel<ResponseDTO>> createdUsers(@Validated @RequestBody UsersRegisterDTO usersRegisterDTO){

        UsersRegisterDTO users = usersRegisterService.createdUsers(usersRegisterDTO);

        // 생성된 User 리소스 번호를 반환
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("UserNo : "+users.getUserNo()));

        // 상품 목록 리스트 링크 생성
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        // 로그인 링크 생성
        Link loginLink = linkTo(methodOn(this.getClass()).login(null, null)).withRel("Log-in");

        entityModel.add(allProductsListLink);
        entityModel.add(loginLink);

        // 201 CREATED Status Code 와 HTTP Body 회원 생성 번호와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @PostMapping("/users/login")
    public ResponseEntity<EntityModel<ResponseDTO>> login(@Validated @RequestBody LoginDTO loginDTO, HttpSession session){

        loginService.login(loginDTO,session);

        // 로그인 생성 메세지를 생성해 반환
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Login : Success"));

        // 로그아웃 링크 생성
        Link logoutLink = linkTo(methodOn(this.getClass()).logout(null)).withRel("Log-out");
        // 상품 목록 추가 링크 생성
        Link createProductsLink = linkTo(methodOn(ProductsController.class).createProducts(null, null)).withRel("Create-Products");
        // 상품 목록 리스트 및 검색 링크 생성
        Link allProductsLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");

        entityModel.add(logoutLink);
        entityModel.add(createProductsLink);
        entityModel.add(allProductsLink);

        // 200 OK Status Code 와 HTTP Body 로그인 성공 메세지와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @PostMapping("/users/logout")
    public ResponseEntity<EntityModel<ResponseDTO>> logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session != null){
            // 세션 삭제
            session.invalidate();
        }

        // 로그아웃 생성 메세지를 생성해 반환
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Logout : Success"));

        // 회원가입 링크 생성
        Link createUsersLink = linkTo(methodOn(this.getClass()).createdUsers(null)).withRel("Create-Users");
        // 상품 목록 리스트 및 검색 링크 생성
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        // 로그인 링크 생성
        Link loginLink = linkTo(methodOn(this.getClass()).login(null,null)).withRel("Log-in");

        entityModel.add(loginLink);
        entityModel.add(createUsersLink);
        entityModel.add(allProductsListLink);

        // 200 OK Status Code 와 HTTP Body 로그아웃 성공 메세지와 링크를 담아서 반환
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
