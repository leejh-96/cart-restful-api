package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.dto.LoginDTO;
import com.outliercart.restfulservice.exception.UserNotFoundException;
import com.outliercart.restfulservice.repository.LoginDao;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginService {

    private final EncryptPasswordService passwordService;

    private final LoginDao loginDao;

    public LoginService(EncryptPasswordService passwordService, LoginDao loginDao) {
        this.passwordService = passwordService;
        this.loginDao = loginDao;
    }

    public void login(LoginDTO loginDTO, HttpSession session) {

        //이미 로그인한 회원인지 세션 체크
        duplicateLoginCheck(session);

        //DB에서 클라이언트가 입력한 ID와 일치하는 USER의 정보를 가져옴.
        LoginDTO user = loginDao.findByUser(loginDTO.getUserId());


        //DB에서 가져온 USER 정보가 NULL인지 체크, NULL이 아니라면 일치하는 USER 정보가 있음.
        idMismatchCheck(user);

        //클라이언트의 비밀번호 입력값과 DB의 비밀번호가 동일한지 체크
        passwordMismatchCheck(loginDTO,user);

        //세션에 USER 저장.
        session.setAttribute("loginMember",user.getUserNo());
    }

    private void duplicateLoginCheck(HttpSession session){
        Long loginMember = (Long) session.getAttribute("loginMember");
        if (loginMember != null)
            throw new UserNotFoundException("이미 로그인 하셨습니다.");
    }

    private void idMismatchCheck(LoginDTO user){
        if (user == null)
            throw new UserNotFoundException("일치하지 않는 아이디입니다.");
    }

    private void passwordMismatchCheck(LoginDTO loginDTO, LoginDTO user) {
        if (!passwordService.checkPassword(loginDTO.getUserPassword(), user.getUserPassword()))
            throw new UserNotFoundException("일치하지 않는 비밀번호입니다.");
    }

    public Long userLoginCheck(HttpSession session) {
        Long userNo = (Long) session.getAttribute("loginMember");
        if (userNo == null)
            throw new UserNotFoundException("로그인 후 사용할 수 있습니다.");
        return userNo;
    }

}
