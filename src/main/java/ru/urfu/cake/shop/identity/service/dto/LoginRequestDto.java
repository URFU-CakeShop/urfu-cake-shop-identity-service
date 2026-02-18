package ru.urfu.cake.shop.identity.service.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}