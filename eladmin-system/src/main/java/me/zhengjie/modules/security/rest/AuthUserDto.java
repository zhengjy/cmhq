package me.zhengjie.modules.security.rest;

import lombok.Data;

@Data
public class AuthUserDto {
    private String username;
    private String password;
}
