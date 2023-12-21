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
    public ResponseEntity<EntityModel<ResponseDTO>> createUser(@Validated @RequestBody UsersRegisterDTO usersRegisterDTO){

        UsersRegisterDTO users = usersRegisterService.save(usersRegisterDTO);

        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("UserNo : "+users.getUserNo()));

        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link loginLink = linkTo(methodOn(this.getClass()).login(null, null)).withRel("Log-in");

        entityModel.add(allProductsListLink);
        entityModel.add(loginLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @PostMapping("/users/login")
    public ResponseEntity<EntityModel<ResponseDTO>> login(@Validated @RequestBody LoginDTO loginDTO, HttpSession session){

        loginService.login(loginDTO,session);

        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Login : Success"));

        Link logoutLink = linkTo(methodOn(this.getClass()).logout(null)).withRel("Log-out");
        Link createProductsLink = linkTo(methodOn(ProductsController.class).createProducts(null, null)).withRel("Create-Products");
        Link allProductsLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");

        entityModel.add(logoutLink);
        entityModel.add(createProductsLink);
        entityModel.add(allProductsLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @PostMapping("/users/logout")
    public ResponseEntity<EntityModel<ResponseDTO>> logout(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if (session != null){
            session.invalidate();
        }

        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Logout : Success"));

        Link createUsersLink = linkTo(methodOn(this.getClass()).createUser(null)).withRel("Create-Users");
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link loginLink = linkTo(methodOn(this.getClass()).login(null,null)).withRel("Log-in");

        entityModel.add(loginLink);
        entityModel.add(createUsersLink);
        entityModel.add(allProductsListLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
