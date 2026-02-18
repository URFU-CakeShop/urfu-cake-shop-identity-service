package ru.urfu.cake.shop.identity.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Модель пользователя")
public class UserModel {

    @Schema(description = "Уникальный идентификатор пользователя")
    private UUID id;

    @Schema(description = "Электронная почта пользователя")
    private String email;

    @Schema(description = "Телефон пользователя")
    private String phoneNumber;

    @Schema(description = "Имя пользователя")
    private String firstName;

    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @Schema(description = "Отчество пользователя")
    private String middleName;

    @Schema(description = "Адрес пользователя")
    @Embedded
    private AddressModel address;

    @Schema(description = "Ест ли у пользователь сахар")
    private Boolean hasSugar;

    @Schema(description = "Дата и время создания пользователя")
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время последнего обновления пользователя")
    private LocalDateTime updatedAt;

    @Schema(description = "Дата и время последнего входа пользователя")
    private LocalDateTime lastLogin;

    @Schema(description = "Идентификатор корзины пользователя")
    private UUID cartId;

    @Schema(description = "Идентификатор аватара пользователя")
    private UUID avatarImageId;

    @Schema(description = "Дополнительные изображения пользователя")
    private Set<Long> imageIds;

    @Schema(description = "Роли пользователя")
    private Set<String> roles;

    @Schema(description = "Описание, которое может редактировать сам пользователь")
    private String publicDescription;

    @Schema(description = "Внутреннее описание пользователя (видимое только поварам/админу)")
    private String internalDescription;
}