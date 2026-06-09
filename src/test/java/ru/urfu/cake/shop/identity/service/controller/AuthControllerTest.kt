package ru.urfu.cake.shop.identity.service.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.urfu.cake.shop.identity.service.dto.LoginRequestDto
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto
import ru.urfu.cake.shop.identity.service.exception.GlobalExceptionHandler
import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException
import ru.urfu.cake.shop.identity.service.model.UserModel
import ru.urfu.cake.shop.identity.service.service.UserService
import java.util.*

@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler::class)
internal class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    // Хелпер для обхода ограничений Kotlin на non-null матчеры
    private fun <T> anyObject(): T {
        Mockito.any<T>()
        return null as T
    }

    @Test
    fun login_success() {
        val userModel = UserModel(
            id = UUID.randomUUID(),
            email = "user@example.com",
            roles = setOf("USER")
        )

        Mockito.`when`(
            userService.login(
                "user@example.com",
                "password123"
            )
        ).thenReturn(userModel)

        val request = LoginRequestDto(
            email = "user@example.com",
            password = "password123"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Login successful"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("user@example.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.roles[0]").value("USER"))
    }

    @Test
    fun login_invalidCredentials() {
        Mockito.`when`(userService.login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
            .thenThrow(InvalidCredentialsException())

        val request = LoginRequestDto(
            email = "user@example.com",
            password = "wrong"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Неверный email или пароль"))
    }

    @Test
    fun login_validationError() {
        val request = LoginRequestDto(
            email = "",
            password = ""
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.containsString("email")))
    }

    @Test
    fun register_success() {
        val userModel = UserModel(
            id = UUID.randomUUID(),
            email = "new@example.com",
            roles = setOf("USER")
        )

        Mockito.`when`(userService.register(anyObject())).thenReturn(userModel)

        val request = UserRegistrationDto(
            email = "new@example.com",
            password = "password123"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Registration successful"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("new@example.com"))
    }

    @Test
    fun register_emailAlreadyExists() {
        Mockito.`when`(userService.register(anyObject()))
            .thenThrow(UserAlreadyExistsException("exists@example.com"))

        val request = UserRegistrationDto(
            email = "exists@example.com",
            password = "password123"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.containsString("exists@example.com")))
    }

    @Test
    fun register_validationError_shortPassword() {
        val request = UserRegistrationDto(
            email = "user@example.com",
            password = "short"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.containsString("password")))
    }
}