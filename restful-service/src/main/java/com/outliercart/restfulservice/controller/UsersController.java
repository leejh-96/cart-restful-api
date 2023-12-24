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

    /**
     * 새로운 사용자 생성 및 관련 링크 반환
     *
     * @param usersRegisterDTO 새로운 사용자 정보
     * @link 상품 목록 리스트 및 검색, 로그인
     * @return 201 CREATED Status Code 와 HTTP Body 회원 생성 번호와 링크를 담아서 반환
     */
    @PostMapping("/users")
    public ResponseEntity<EntityModel<ResponseDTO>> createdUsers(@Validated @RequestBody UsersRegisterDTO usersRegisterDTO){
        /* 사용자 생성 */
        UsersRegisterDTO users = usersRegisterService.createdUsers(usersRegisterDTO);

        /* 생성된 사용자 번호를 ResponseDTO(응답 객체)에 담아 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("UserNo : "+users.getUserNo()));

        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link loginLink = linkTo(methodOn(this.getClass()).login(null, null)).withRel("Log-in");

        entityModel.add(allProductsListLink);
        entityModel.add(loginLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    /**
     * 로그인 처리 및 관련 링크 제공
     *
     * @param loginDTO 로그인 정보
     * @param session 현재 세션 정보
     * @link 로그아웃, 상품 목록 리스트 및 검색, 상품 목록 추가
     * @return 200 OK Status Code 와 HTTP Body 로그인 성공 메세지와 링크를 담아서 반환
     */
    @PostMapping("/users/login")
    public ResponseEntity<EntityModel<ResponseDTO>> login(@Validated @RequestBody LoginDTO loginDTO, HttpSession session){
        /* 로그인 처리 */
        loginService.login(loginDTO,session);

        /* 로그인 성공 메세지를 ResponseDTO(응답 객체)에 담아 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Login : Success"));

        Link logoutLink = linkTo(methodOn(this.getClass()).logout(null)).withRel("Log-out");
        Link createProductsLink = linkTo(methodOn(ProductsController.class).createProducts(null, null)).withRel("Create-Products");
        Link allProductsLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");

        entityModel.add(logoutLink);
        entityModel.add(createProductsLink);
        entityModel.add(allProductsLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    /**
     * 로그아웃 처리 및 관련 링크 제공
     *
     * @param request HTTP 요청 정보
     * @link 회원가입, 상품 목록 리스트 및 검색, 로그인
     * @return 200 OK Status Code 와 HTTP Body 로그아웃 성공 메세지와 링크를 담아서 반환
     */
    @PostMapping("/users/logout")
    public ResponseEntity<EntityModel<ResponseDTO>> logout(HttpServletRequest request){
        /* 기존 세션이 있을 경우 세션을 반환하며, 세션이 없을 때는 새로운 세션을 생성하지 않고 null을 반환 */
        HttpSession session = request.getSession(false);

        /* 세션 삭제 */
        if (session != null){
            session.invalidate();
        }

        /* 로그아웃 성공 메세지를 ResponseDTO(응답 객체)에 담아 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("Logout : Success"));

        Link createUsersLink = linkTo(methodOn(this.getClass()).createdUsers(null)).withRel("Create-Users");
        Link allProductsListLink = linkTo(methodOn(ProductsController.class).allProducts(null)).withRel("All-Products-List");
        Link loginLink = linkTo(methodOn(this.getClass()).login(null,null)).withRel("Log-in");

        entityModel.add(loginLink);
        entityModel.add(createUsersLink);
        entityModel.add(allProductsListLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
