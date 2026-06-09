package ru.urfu.cake.shop.identity.service.service

import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto
import ru.urfu.cake.shop.identity.service.model.UserModel

interface UserService {
    fun login(email: String, rawPassword: String): UserModel

    fun register(dto: UserRegistrationDto): UserModel
}