package ru.urfu.cake.shop.identity.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.urfu.cake.shop.identity.service.dto.LoginRequestDto;
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto;
import ru.urfu.cake.shop.identity.service.exception.GlobalExceptionHandler;
import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException;
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException;
import ru.urfu.cake.shop.identity.service.model.UserModel;
import ru.urfu.cake.shop.identity.service.service.UserService;

import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void login_success() throws Exception {
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .roles(Set.of("USER"))
                .build();

        when(userService.login(eq("user@example.com"), eq("password123"))).thenReturn(userModel);

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.roles[0]").value("USER"));
    }

    @Test
    void login_invalidCredentials() throws Exception {
        when(userService.login(any(), any())).thenThrow(new InvalidCredentialsException());

        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("user@example.com");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Неверный email или пароль"));
    }

    @Test
    void login_validationError() throws Exception {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("email")));
    }

    @Test
    void register_success() throws Exception {
        UserModel userModel = UserModel.builder()
                .id(UUID.randomUUID())
                .email("new@example.com")
                .roles(Set.of("USER"))
                .build();

        when(userService.register(any(UserRegistrationDto.class))).thenReturn(userModel);

        UserRegistrationDto request = new UserRegistrationDto();
        request.setEmail("new@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }

    @Test
    void register_emailAlreadyExists() throws Exception {
        when(userService.register(any(UserRegistrationDto.class)))
                .thenThrow(new UserAlreadyExistsException("exists@example.com"));

        UserRegistrationDto request = new UserRegistrationDto();
        request.setEmail("exists@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("exists@example.com")));
    }

    @Test
    void register_validationError_shortPassword() throws Exception {
        UserRegistrationDto request = new UserRegistrationDto();
        request.setEmail("user@example.com");
        request.setPassword("short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("password")));
    }
}
