package com.outliercart.restfulservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter@Setter
public class LoginDTO {

    private Long userNo;//유저 번호

    @NotBlank(message = "아이디를 작성해주세요.")
    private String userId;//유저 아이디

    @NotBlank(message = "비밀번호를 작성해주세요.")
    private String userPassword;//유저 비밀번호
}
