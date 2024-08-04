package me.zhengjie.modules.security.config;

import lombok.Data;

@Data
public class AuthUserDto {
    private String username;
    private String password;

    private String mobile;
    private String newpassword;
}
