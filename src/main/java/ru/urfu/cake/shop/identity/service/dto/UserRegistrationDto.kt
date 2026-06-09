package ru.urfu.cake.shop.identity.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "email обязателен")
    @Email(message = "некорректный формат email")
    private String email;

    @NotBlank(message = "password обязателен")
    @Size(min = 8, message = "password должен содержать минимум 8 символов")
    private String password;

    private String firstName;
    private String lastName;
    private String middleName;

    private String city;
    private String street;
    private String house;
    private String apartment;

    private String phoneNumber;
    private Boolean hasSugar;

    private String publicDescription;
}
