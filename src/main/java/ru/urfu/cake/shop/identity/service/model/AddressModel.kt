package ru.urfu.cake.shop.identity.service.model

import io.swagger.v3.oas.annotations.media.Schema

data class AddressModel(
    @field:Schema(description = "Город проживания пользователя", example = "Екатеринбург")
    val city: String,

    @field:Schema(description = "Улица", example = "Ленина")
    val street: String,

    @field:Schema(description = "Номер дома", example = "15")
    val house: String,

    @field:Schema(description = "Номер квартиры", example = "42")
    val apartment: String,
)