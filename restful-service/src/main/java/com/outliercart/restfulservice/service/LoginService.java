package com.outliercart.restfulservice.service;

import com.outliercart.restfulservice.dto.LoginDTO;
import com.outliercart.restfulservice.exception.UserNotFoundException;
import com.outliercart.restfulservice.repository.LoginDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final EncryptPasswordService passwordService;

    private final LoginDao loginDao;

    public LoginService(EncryptPasswordService passwordService, LoginDao loginDao) {
        this.passwordService = passwordService;
        this.loginDao = loginDao;
    }

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param loginDTO 로그인 정보
     * @param session HTTP 세션
     * @throws UserNotFoundException 로그인 예외 처리
     */
    public void login(LoginDTO loginDTO, HttpSession session) {

        /* 이미 로그인한 사용자인지 확인 */
        Long loginMember = (Long) session.getAttribute("loginMember");
        if (loginMember != null)
            throw new UserNotFoundException("이미 로그인 하셨습니다.");

        /* 입력된 사용자 정보와 DB의 정보 비교 */
        LoginDTO byUser = loginDao.findByUser(loginDTO.getUserId());
        if (byUser == null)
            throw new UserNotFoundException("일치하지 않는 아이디입니다.");

        /* 비밀번호 일치 여부 확인 */
        if (!passwordService.checkPassword(loginDTO.getUserPassword(), byUser.getUserPassword()))
            throw new UserNotFoundException("일치하지 않는 비밀번호입니다.");

        /* 세션에 사용자 정보 저장 */
        session.setAttribute("loginMember",byUser.getUserNo());
    }

    /**
     * 사용자 로그인 여부를 확인합니다.
     *
     * @param session HTTP 세션
     * @return 로그인한 사용자의 번호
     * @throws UserNotFoundException 로그인 예외 처리
     */
    public Long userLoginCheck(HttpSession session) {
        /* 세션을 통해 사용자 번호 확인 */
        Long userNo = (Long) session.getAttribute("loginMember");

        /* 로그인 여부에 따른 예외 처리 혹은 사용자 번호 반환 */
        if (userNo == null)
            throw new UserNotFoundException("로그인 후 사용할 수 있습니다.");
        return userNo;
    }

}
