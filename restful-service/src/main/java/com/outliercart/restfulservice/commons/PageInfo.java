package com.outliercart.restfulservice.commons;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Getter@Setter
@Slf4j
public class PageInfo {

    private String searchType;
    private String searchContent;

    private int page;
    private int count;
    private int postLimit = 10;
    private int pageLimit = 10;

    public void pageSettings(){
        log.info("getpage: "+getPage());
        if (getPage()==0){
            setPage(1);
        }
        //조금 더 생각해보고 삭제하기
        log.info("getEndPage : "+getEndPage());
        if (getPage() > getEndPage())
            setPage(1);
//            throw new PageNotFoundException("존재하지 않는 페이지 입니다.");
    }

    public void prevPageSettings(){
        log.info("getpage: "+getPage());
        if (getPage()==0){
            setPage(1);
        }
    }

    public int getMaxPage(){
        return (int)Math.ceil((double)count / pageLimit);
    }

    public int getStartPage(){
        return (postLimit * ((page - 1) / postLimit)) + 1;
    }

    public int getEndPage(){
        int endPage = this.getStartPage() + postLimit - 1;
        return endPage > this.getMaxPage() ? this.getMaxPage() : endPage;
    }

    public int getPrevPage() {
        int prevPage = getPage() - 1;
        return prevPage < 1 ? 1 : prevPage;
    }

    public int getNextPage() {
        int nextPage = getPage() + 1;
        return nextPage > this.getMaxPage() ? this.getMaxPage() : nextPage;
    }

    public int getStartList() {
        return (getPage() - 1) * pageLimit + 1;
    }

    public int getEndList() {
        int endList = this.getStartList() + pageLimit - 1;
        return endList > count ? count : endList;
    }

    public int getOffset(){
        int currentPage = getPage() == 0 ? 1 : getPage();
        return (currentPage - 1) * getPageLimit();
    }

}
