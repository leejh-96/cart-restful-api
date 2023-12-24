package com.outliercart.restfulservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
public class UsersRegisterDTO {

    private Long userNo;//생성된 유저 번호

    @NotBlank(message = "아이디를 작성해주세요.")
    private String userId;//유저 아이디

    @NotBlank(message = "비밀번호를 작성해주세요.")
    private String userPassword;//유저 비밀번호

    @NotBlank(message = "이름을 작성해주세요.")
    private String userName;//유저 이름

    @Email(message = "이메일을 올바르게 작성해주세요.")
    private String userEmail;//유저 이메일

}
