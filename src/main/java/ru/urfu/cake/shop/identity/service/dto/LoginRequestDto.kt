package ru.urfu.cake.shop.identity.service.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:NotBlank(message = "email обязателен")
    @field:Email(message = "некорректный формат email")
    val email: String = "",

    @field:NotBlank(message = "password обязателен")
    val password: String = ""
)