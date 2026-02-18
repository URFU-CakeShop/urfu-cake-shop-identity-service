package ru.urfu.cake.shop.identity.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.urfu.cake.shop.identity.service.dto.UserRegistrationDto;
import ru.urfu.cake.shop.identity.service.entity.Address;
import ru.urfu.cake.shop.identity.service.entity.Role;
import ru.urfu.cake.shop.identity.service.entity.User;
import ru.urfu.cake.shop.identity.service.exception.UserAlreadyExistsException;
import ru.urfu.cake.shop.identity.service.model.AddressModel;
import ru.urfu.cake.shop.identity.service.model.UserModel;
import ru.urfu.cake.shop.identity.service.repository.RoleRepository;
import ru.urfu.cake.shop.identity.service.repository.UserRepository;
import ru.urfu.cake.shop.identity.service.util.TimeUtil;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }

        // Обновляем время последнего входа
        user.setLastLogin(TimeUtil.now());
        userRepository.save(user);

        return user;
    }

    public User register(UserRegistrationDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(dto.getEmail());
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(TimeUtil.now());
        user.setUpdatedAt(TimeUtil.now());

        // Личные данные
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setMiddleName(dto.getMiddleName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setHasSugar(dto.getHasSugar());


        if (dto.getCity() != null || dto.getStreet() != null || dto.getHouse() != null || dto.getApartment() != null) {
            Address address = new Address();
            address.setCity(dto.getCity());
            address.setStreet(dto.getStreet());
            address.setHouse(dto.getHouse());
            address.setApartment(dto.getApartment());
            user.setAddress(address);
        }

        // Описания
        user.setPublicDescription(dto.getPublicDescription());

        // Связи с другими сервисами (пока null, создаются отдельным процессом)
        user.setCartId(null);
        user.setAvatarImageId(null);

        // Роль пользователя
        Role userRole = roleRepository.findByRole("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER не найдена"));

        user.getRoles().add(userRole);

        userRepository.save(user);

        return user;
    }

    /**
     * Преобразование сущности User в модель для API
     */
    public UserModel toModel(User user) {
        return UserModel.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .address(toModel(user.getAddress()))
                .phoneNumber(user.getPhoneNumber())
                .hasSugar(user.getHasSugar())
                .cartId(user.getCartId())
                .avatarImageId(user.getAvatarImageId())
                .publicDescription(user.getPublicDescription())
                .internalDescription(user.getInternalDescription())
                .roles(user.getRoles().stream()
                        .map(Role::getRole)
                        .collect(Collectors.toSet()))
                .build();
    }

    private AddressModel toModel(Address address) {
        if (address == null) return null;

        return AddressModel.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .apartment(address.getApartment())
                .build();
    }
}
