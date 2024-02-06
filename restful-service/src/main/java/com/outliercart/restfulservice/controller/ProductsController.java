package com.outliercart.restfulservice.controller;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.dto.ProductsDTO;
import com.outliercart.restfulservice.dto.ResponseDTO;
import com.outliercart.restfulservice.service.CreateLinkService;
import com.outliercart.restfulservice.service.LoginService;
import com.outliercart.restfulservice.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
public class ProductsController {

    private final CreateLinkService createLinkService;

    private final LoginService loginService;

    private final ProductService productService;

    public ProductsController(CreateLinkService createLinkService, LoginService loginService, ProductService productService) {
        this.createLinkService = createLinkService;
        this.loginService = loginService;
        this.productService = productService;
    }

    /**
     * 새로운 상품 생성 및 관련 링크 반환
     *
     * @param productsDTO 새로운 상품 정보
     * @param session 현재 세션 정보
     * @link 상품 목록 리스트 및 검색, 상품 상세 보기
     * @return 201 CREATED HTTP Status Code 와 HTTP Body 생성된 상품 번호와 링크를 담아서 반환
     */
    @PostMapping("/products")
    public ResponseEntity<EntityModel<ResponseDTO>> createProducts(@Validated @RequestBody ProductsDTO productsDTO, HttpSession session){
        /* 로그인 체크 */
        loginService.userLoginCheck(session);

        /* 상품 생성 */
        ProductsDTO productDTO = productService.createdProducts(productsDTO);

        /* 생성된 상품 번호를 ResponseDTO(응답 객체)에 담아 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<ResponseDTO> entityModel = EntityModel.of(new ResponseDTO("ProductNo : "+productDTO.getProductNo()));

        Link allProductsListLink = linkTo(methodOn(this.getClass()).allProducts(null)).withRel("All-Products-List");
        Link selectedProductsLink = linkTo(methodOn(this.getClass()).selectedProducts(productDTO.getProductNo(), null)).withRel("Selected-Products");

        entityModel.add(allProductsListLink);
        entityModel.add(selectedProductsLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    /**
     * 모든 상품 조회 및 관련 링크 제공
     *
     * @param pageInfo 페이지 정보
     * @link 상품 목록 리스트, 장바구니 목록 리스트, 구매 목록 리스트, 페이징 처리 링크
     * @return 200 OK HTTP Status Code 와 HTTP Body 상품 목록 검색 리스트와 링크를 담아서 반환
     */
    @GetMapping("/products")
    public ResponseEntity<CollectionModel<ProductsDTO>> allProducts(@ModelAttribute PageInfo pageInfo){
        /* 현재 요청에 대한 페이징 처리 */
        pageInfo.setCount(productService.allProductsCount(pageInfo));
        pageInfo.pageSettings();

        /* 데이터베이스에서 상품 목록 반환 */
        List<ProductsDTO> productsList = productService.allProductsList(pageInfo);

        /* 상품 상세 보기 링크를 생성해 ProductDTO 객체에 추가 */
        for (ProductsDTO dto : productsList){
            UriComponents selectedProductUri = ServletUriComponentsBuilder.fromCurrentRequest()
                                                                        .path("/{productNo}")
                                                                        .replaceQueryParam("searchType", pageInfo.getSearchType())
                                                                        .replaceQueryParam("searchContent", pageInfo.getSearchContent())
                                                                        .replaceQueryParam("page", pageInfo.getPage())
                                                                        .buildAndExpand(dto.getProductNo());
            /* 상품 상세 보기 링크 생성 후 DTO에 담기 */
            dto.add(Link.of(selectedProductUri.toUriString()).withRel("Selected-Products"));
        }

        /* productsList객체를 CollectionModel 변환하여 각 ProductsDTO에 HATEOAS 링크를 포함시킴 */
        CollectionModel<ProductsDTO> collectionModel = CollectionModel.of(productsList);

        Link createProductsLink = linkTo(methodOn(this.getClass()).createProducts(null, null)).withRel("Create-Products");
        Link allCartsListLink = linkTo(methodOn(CartsController.class).allCarts(null, null)).withRel("All-Carts-List");
        Link allPurchaseListLink = linkTo(methodOn(PurchasesController.class).allPurchasesItems(null, null)).withRel("All-Purchase-List");
        /* 상품 목록 페이징 처리를 위한 쿼리 파라미터 세팅 후 페이징 HATEOAS 링크 반환 */
        List<Link> paginationLinks = createLinkService.createPaginationLinks(this.getClass(), pageInfo);

        /* 생성된 페이징 링크를 collectionModel에 순서대로 추가 */
        for (Link link : paginationLinks){

            collectionModel.add(link);
        }
        collectionModel.add(createProductsLink);
        collectionModel.add(allCartsListLink);
        collectionModel.add(allPurchaseListLink);

        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    /**
     * 특정 상품 조회 및 관련 링크 제공
     *
     * @param productNo 조회할 상품 번호
     * @param pageInfo 페이지 정보
     * @link 이전 상품 목록으로 돌아가기, 장바구니 담기
     * @return 200 OK HTTP Status Code 와 HTTP Body 상품 상세 정보와 링크를 담아서 반환
     */
    @GetMapping("/products/{productNo}")
    public ResponseEntity<EntityModel<ProductsDTO>> selectedProducts(@PathVariable int productNo, @ModelAttribute PageInfo pageInfo){
        /* 상품 목록으로 돌아가기 위해 이전 페이지 번호 세팅 */
        pageInfo.prevPageSettings();

        /* 데이터베이스에서 상품 정보 반환 */
        ProductsDTO selectedProduct = productService.selectedProducts(productNo);

        /* selectedProduct 객체를 EntityModel로 변환하여 HATEOAS 링크를 포함시킴 */
        EntityModel<ProductsDTO> entityModel = EntityModel.of(selectedProduct);

        /* 이전으로 돌아가기 위한 이전 페이지 정보 쿼리 파라미터 세팅 */
        UriComponentsBuilder productsList = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                    .path("/products")
                                                                    .replaceQueryParam("searchType", pageInfo.getSearchType())
                                                                    .replaceQueryParam("searchContent", pageInfo.getSearchContent())
                                                                    .replaceQueryParam("page", pageInfo.getPage());

        Link prevProductListLink = Link.of(productsList.toUriString(), "Prev-By-Product-List");
        Link createCartLink = linkTo(methodOn(CartsController.class).createCarts(null, null)).withRel("Create-Carts");

        entityModel.add(prevProductListLink);
        entityModel.add(createCartLink);

        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

}
