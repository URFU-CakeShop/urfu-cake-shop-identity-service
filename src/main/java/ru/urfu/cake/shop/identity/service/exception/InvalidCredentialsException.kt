package ru.urfu.cake.shop.identity.service.exception;


public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Неверный email или пароль");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
