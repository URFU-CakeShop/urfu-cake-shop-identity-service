package ru.urfu.cake.shop.identity.service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.urfu.cake.shop.identity.service.controller.AuthController;
import ru.urfu.cake.shop.identity.service.dto.ApiResponse;
import ru.urfu.cake.shop.identity.service.dto.LoginRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleUserAlreadyExists() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleUserAlreadyExists(new UserAlreadyExistsException("user@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("user@example.com");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void handleInvalidCredentials() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleInvalidCredentials(new InvalidCredentialsException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Неверный email или пароль");
    }

    @Test
    void handleValidation() throws NoSuchMethodException {
        LoginRequestDto dto = new LoginRequestDto();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "loginRequestDto");
        bindingResult.rejectValue("email", "NotBlank", "email обязателен");

        MethodParameter methodParameter = new MethodParameter(
                AuthController.class.getDeclaredMethod("login", LoginRequestDto.class), 0);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ApiResponse<Void>> response = handler.handleValidation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("email: email обязателен");
    }

    @Test
    void handleOtherExceptions() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleOtherExceptions(new RuntimeException("database connection failed"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("Произошла внутренняя ошибка");
    }
}
