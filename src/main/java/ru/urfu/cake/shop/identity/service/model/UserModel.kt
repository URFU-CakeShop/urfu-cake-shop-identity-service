package ru.urfu.cake.shop.identity.service.model

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Embedded
import java.time.LocalDateTime
import java.util.UUID

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Модель пользователя")
data class UserModel(
    @field:Schema(description = "Уникальный идентификатор пользователя")
    val id: UUID? = null,

    @field:Schema(description = "Электронная почта пользователя")
    val email: String? = null,

    @field:Schema(description = "Телефон пользователя")
    val phoneNumber: String? = null,

    @field:Schema(description = "Имя пользователя")
    val firstName: String? = null,

    @field:Schema(description = "Фамилия пользователя")
    val lastName: String? = null,

    @field:Schema(description = "Отчество пользователя")
    val middleName: String? = null,

    @field:Schema(description = "Адрес пользователя")
    @Embedded
    val address: AddressModel? = null,

    @field:Schema(description = "Ест ли у пользователь сахар")
    val hasSugar: Boolean? = null,

    @field:Schema(description = "Дата и время создания пользователя")
    val createdAt: LocalDateTime? = null,

    @field:Schema(description = "Дата и время последнего обновления пользователя")
    val updatedAt: LocalDateTime? = null,

    @field:Schema(description = "Дата и время последнего входа пользователя")
    val lastLogin: LocalDateTime? = null,

    @field:Schema(description = "Идентификатор корзины пользователя")
    val cartId: UUID? = null,

    @field:Schema(description = "Идентификатор аватара пользователя")
    val avatarImageId: UUID? = null,

    @field:Schema(description = "Дополнительные изображения пользователя")
    val imageIds: Set<Long>? = null,

    @field:Schema(description = "Роли пользователя")
    val roles: Set<String>? = null,

    @field:Schema(description = "Описание, которое может редактировать сам пользователь")
    val publicDescription: String? = null,

    @field:Schema(description = "Внутреннее описание пользователя (видимое только поварам/админу)")
    val internalDescription: String? = null
)