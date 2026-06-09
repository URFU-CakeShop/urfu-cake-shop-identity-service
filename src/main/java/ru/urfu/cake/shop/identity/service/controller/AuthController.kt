package ru.urfu.cake.shop.identity.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.cake.shop.identity.service.dto.ApiResponse;
import ru.urfu.cake.shop.identity.service.dto.LoginRequestDto;
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto;
import ru.urfu.cake.shop.identity.service.model.UserModel;
import ru.urfu.cake.shop.identity.service.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserModel>> login(@Valid @RequestBody LoginRequestDto request) {
        UserModel user = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, user, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserModel>> register(@Valid @RequestBody UserRegistrationDto dto) {
        UserModel user = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, user, "Registration successful"));
    }
}
