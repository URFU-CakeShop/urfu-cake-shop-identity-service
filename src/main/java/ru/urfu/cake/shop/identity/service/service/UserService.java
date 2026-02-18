package ru.urfu.cake.shop.identity.service.service;

import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto;
import ru.urfu.cake.shop.identity.service.entity.User;
import ru.urfu.cake.shop.identity.service.model.UserModel;

public interface UserService {

    /**
     * Логин пользователя
     */
    User login(String email, String rawPassword);

    /**
     * Регистрация нового пользователя
     */
    User register(UserRegistrationDto dto);

    /**
     * Формирование response
     */
    UserModel toModel(User user);
}
