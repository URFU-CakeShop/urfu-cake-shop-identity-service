package ru.urfu.cake.shop.identity.service.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Пользователь с email '" + email + "' уже существует");
    }
}