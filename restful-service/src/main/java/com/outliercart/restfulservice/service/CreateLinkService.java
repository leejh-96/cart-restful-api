package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.commons.PageInfo;
import com.outliercart.restfulservice.controller.ProductsController;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateLinkService {

    /**
     * 페이지네이션을 위한 링크 목록을 생성합니다.
     *
     * @param controllerClass 컨트롤러 클래스
     * @param pageInfo 페이지 정보
     * @return 페이지네이션을 위한 링크 목록
     */
    public List<Link> createPaginationLinks(Class<?> controllerClass, PageInfo pageInfo) {

        List<Link> linksList = new ArrayList<>();
        UriComponentsBuilder builder = null;

        if (controllerClass.getName().equals(ProductsController.class.getName())){
            /* 상품 목록 페이징 처리를 위한 쿼리 파라미터 세팅 */
            builder = ServletUriComponentsBuilder.fromCurrentRequest()
                                                .replaceQueryParam("searchType", pageInfo.getSearchType())
                                                .replaceQueryParam("searchContent", pageInfo.getSearchContent())
                                                .replaceQueryParam("page", pageInfo.getStartPage());
        }else {
            /* 장바구니 목록 페이징, 구매 목록 페이징 처리를 위한 쿼리 파라미터 세팅 */
            builder = ServletUriComponentsBuilder.fromCurrentRequest()
                                                .replaceQueryParam("page", pageInfo.getStartPage());
        }

        /* 가장 첫번째 페이지 링크 생성 */
        Link startPageLink = Link.of(builder.toUriString(), "Start-Page");

        /* 이전 페이지 링크 생성 */
        builder.replaceQueryParam("page", pageInfo.getPrevPage());
        Link prevPageLink = Link.of(builder.toUriString(), "Prev-Page");

        /* 현재 페이지 링크 생성 */
        builder.replaceQueryParam("page", pageInfo.getPage());
        Link currentPageLink = Link.of(builder.toUriString(), "Current-Page");

        /* 다음 페이지 링크 생성 */
        builder.replaceQueryParam("page", pageInfo.getNextPage());
        Link nextPageLink = Link.of(builder.toUriString(), "Next-Page");

        /* 맨 마지막 페이지 링크 생성 */
        builder.replaceQueryParam("page", pageInfo.getEndPage());
        Link endPageLink = Link.of(builder.toUriString(), "End-Page");

        linksList.add(startPageLink);
        linksList.add(prevPageLink);
        linksList.add(currentPageLink);
        linksList.add(nextPageLink);
        linksList.add(endPageLink);

        return linksList;
    }

}
