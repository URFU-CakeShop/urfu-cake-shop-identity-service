package ru.urfu.cake.shop.identity.service.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserRegistrationDto(
    @field:NotBlank(message = "email обязателен")
    @field:Email(message = "некорректный формат email")
    val email: String = "",

    @field:NotBlank(message = "password обязателен")
    @field:Size(min = 8, message = "password должен содержать минимум 8 символов")
    val password: String = "",

    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,

    val city: String? = null,
    val street: String? = null,
    val house: String? = null,
    val apartment: String? = null,

    val phoneNumber: String? = null,
    val hasSugar: Boolean = false,

    val publicDescription: String? = null
)