package ru.urfu.cake.shop.identity.service.exception

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import ru.urfu.cake.shop.identity.service.controller.AuthController
import ru.urfu.cake.shop.identity.service.dto.LoginRequestDto
import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException

internal class GlobalExceptionHandlerTest {

    private lateinit var handler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        handler = GlobalExceptionHandler()
    }

    @Test
    fun handleUserAlreadyExists() {
        val response = handler.handleUserAlreadyExists(UserAlreadyExistsException("user@example.com"))

        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(response.body).isNotNull
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).contains("user@example.com")
        assertThat(response.body?.data).isNull()
    }

    @Test
    fun handleInvalidCredentials() {
        val response = handler.handleInvalidCredentials(InvalidCredentialsException())

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.body).isNotNull
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo("Неверный email или пароль")
    }

    @Test
    fun handleValidation() {
        val dto = LoginRequestDto()
        val bindingResult = BeanPropertyBindingResult(dto, "loginRequestDto")
        bindingResult.rejectValue("email", "NotBlank", "email обязателен")

        val method = AuthController::class.java.getDeclaredMethod("login", LoginRequestDto::class.java)
        val methodParameter = MethodParameter(method, 0)
        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)

        val response = handler.handleValidation(exception)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo("email: email обязателен")
    }

    @Test
    fun handleOtherExceptions() {
        val response = handler.handleOtherExceptions(RuntimeException("database connection failed"))

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(response.body).isNotNull
        assertThat(response.body?.success).isFalse()
        assertThat(response.body?.message).isEqualTo("Произошла внутренняя ошибка")
    }
}
