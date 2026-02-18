package ru.urfu.cake.shop.identity.service.controller;

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
import ru.urfu.cake.shop.identity.service.entity.User;
import ru.urfu.cake.shop.identity.service.exception.InvalidCredentialsException;
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException;
import ru.urfu.cake.shop.identity.service.model.UserModel;
import ru.urfu.cake.shop.identity.service.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserModel>> login(@RequestBody LoginRequestDto request) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new ApiResponse<>(true, userService.toModel(user), "Login successful"));
        } catch (InvalidCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserModel>> register(@RequestBody UserRegistrationDto dto) {
        try {
            User user = userService.register(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, userService.toModel(user), "Registration successful"));
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(false, null, ex.getMessage()));
        }
    }
}