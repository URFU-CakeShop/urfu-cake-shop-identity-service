package ru.urfu.cake.shop.identity.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "email обязателен")
    @Email(message = "некорректный формат email")
    private String email;

    @NotBlank(message = "password обязателен")
    private String password;
}
