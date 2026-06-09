package ru.urfu.cake.shop.identity.service.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.urfu.cake.shop.identity.service.dto.ApiResponse
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto
import ru.urfu.cake.shop.identity.service.dto.LoginRequestDto
import ru.urfu.cake.shop.identity.service.model.UserModel
import ru.urfu.cake.shop.identity.service.service.UserService

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequestDto): ResponseEntity<ApiResponse<UserModel>> {
        val user = userService.login(request.email, request.password)
        return ResponseEntity.ok(
            ApiResponse(
                success = true,
                data = user,
                message = "Login successful"
            )
        )
    }

    @PostMapping("/register")
    fun register(@Valid @RequestBody dto: UserRegistrationDto): ResponseEntity<ApiResponse<UserModel>> {
        val user = userService.register(dto)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                ApiResponse(
                    success = true,
                    data = user,
                    message = "Registration successful"
                )
            )
    }
}