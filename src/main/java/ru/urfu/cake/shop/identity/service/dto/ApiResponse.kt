package ru.urfu.cake.shop.identity.service.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val message: String? = null
)