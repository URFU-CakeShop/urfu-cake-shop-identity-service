package ru.urfu.cake.shop.identity.service.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SwaggerConfig {
    @Bean
    open fun customOpenAPI(): OpenAPI? {
        return OpenAPI()
            .info(
                Info()
                    .title("Identity Service API")
                    .version("1.0")
                    .description("API для логина и регистрации пользователей")
            )
    }
}