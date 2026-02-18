package ru.urfu.cake.shop.identity.service.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    // Основные
    private String email;
    private String password;

    // Личные данные
    private String firstName;
    private String lastName;
    private String middleName;

    // Адрес
    private String city;
    private String street;
    private String house;
    private String apartment;

    private String phoneNumber;
    private Boolean hasSugar;

    // Пользовательское описание
    private String publicDescription;
}