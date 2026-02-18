package ru.urfu.cake.shop.identity.service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AddressModel {

    @Schema(description = "Город проживания пользователя", example = "Екатеринбург")
    private String city;

    @Schema(description = "Улица", example = "Ленина")
    private String street;

    @Schema(description = "Номер дома", example = "15")
    private String house;

    @Schema(description = "Номер квартиры", example = "42")
    private String apartment;
}