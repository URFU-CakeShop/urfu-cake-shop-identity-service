package ru.urfu.cake.shop.identity.service.exception

class UserAlreadyExistsException(email: String?) : RuntimeException("Пользователь с email '$email' уже существует")