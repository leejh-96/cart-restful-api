package com.outliercart.restfulservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
public class UsersRegisterDTO {

    private Long userNo;

    @NotBlank(message = "아이디를 작성해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 작성해주세요.")
    private String userPassword;

    @NotBlank(message = "이름을 작성해주세요.")
    private String userName;

    @Email(message = "이메일을 올바르게 작성해주세요.")
    private String userEmail;

}
