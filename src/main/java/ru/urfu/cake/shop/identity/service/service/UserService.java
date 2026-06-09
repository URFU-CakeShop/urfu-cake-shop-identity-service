package ru.urfu.cake.shop.identity.service.service;

import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto;
import ru.urfu.cake.shop.identity.service.model.UserModel;

public interface UserService {

    UserModel login(String email, String rawPassword);

    UserModel register(UserRegistrationDto dto);
}
